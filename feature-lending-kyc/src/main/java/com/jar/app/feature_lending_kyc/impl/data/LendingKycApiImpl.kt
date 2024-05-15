package com.jar.app.feature_lending_kyc.impl.data

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.domain.mapper.toKycProgressResponse
import com.jar.app.core_base.domain.model.KycProgressResponse
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_lending_kyc.api.LendingKycApi
import com.jar.app.feature_lending_kyc.impl.domain.model.KYCScreenArgs
import com.jar.app.feature_lending_kyc.impl.ui.otp.OtpSheetArguments
import com.jar.app.feature_lending_kyc.shared.api.use_case.FetchKycProgressUseCase
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.arguments.CreditReportScreenArguments
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.model.ManualPanEntryScreenArguments
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestCreditReportOtpUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestCreditReportOtpV2UseCase
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class LendingKycApiImpl @Inject constructor(
    private val activity: FragmentActivity,
    private val navControllerRef: Lazy<NavController>,
    private val fetchKycProgressUseCase: FetchKycProgressUseCase,
    private val requestCreditReportOtpUseCase: RequestCreditReportOtpUseCase,
    private val requestCreditReportOtpV2UseCase: RequestCreditReportOtpV2UseCase,
    private val appScope: CoroutineScope,
    private val dispatcher: DispatcherProvider,
    private val serializer: Serializer
) : LendingKycApi, BaseNavigation {

    private var job: Job? = null

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openLendingKyc(flowType: String, progressApiCallback: (String?) -> Unit) {
        job?.cancel()
        job = appScope.launch(dispatcher.main) {
            fetchKycProgressUseCase.fetchKycProgress()
                .mapToDTO {
                    it?.let { it.toKycProgressResponse() }
                }
                .collect(
                    onLoading = {},
                    onSuccess = {
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            progressApiCallback.invoke(null)
                            it?.let {
                                navigate(flowType, it)
                            }
                        }
                    },
                    onError = { errorMessage, errorCode ->
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
                            progressApiCallback.invoke(errorMessage)
                    }
                )
        }
    }

    override fun openLendingKycWithProgressResponse(
        flowType: String,
        progressResponse: KycProgressResponse
    ) {
        navigate(flowType, progressResponse)
    }

    override fun openPANFetchFlow(
        kycFeatureFlowType: KycFeatureFlowType,
        fromScreen: String,
        childNavController: NavController,
        shouldOpenPanInBackground: Boolean,
        shouldNotifyAfterOtpSuccess: Boolean,
        nameForCreditReport: String?,
        panNumberForCreditReport: String?,
        lenderName: String?,
        apiStateCallback: ((Boolean, String?) -> Unit)?
    ) {
        job?.cancel()
        job = appScope.launch(dispatcher.main) {
            val screenArgs = encodeUrl(
                serializer.encodeToString(
                    ManualPanEntryScreenArguments(
                        fromScreen = fromScreen.ifEmpty {
                            BaseConstants.LendingKycFromScreen.LENDING_ONBOARDING
                        },
                        isPanAadhaarMismatch = false,
                        jarVerifiedPAN = false,
                        kycFeatureFlowType = kycFeatureFlowType,
                        lenderName = lenderName
                    )
                )
            )
            if (shouldOpenPanInBackground) {
                childNavController.navigate(
                    Uri.parse(
                        "android-app://com.jar.app/manual-pan/$screenArgs"
                    ),
                    getNavOptions(shouldAnimate = true)
                )
            }
            if (shouldNotifyAfterOtpSuccess) {
                requestCreditReportOtpV2UseCase.requestCreditReportOtp(kycFeatureFlowType).collectUnwrapped(
                    onLoading = {
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            apiStateCallback?.invoke(true, null)
                        }
                    },
                    onSuccess = {
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            apiStateCallback?.invoke(false, null)
                            if (it.success) {
                                navigateToPanOtpScreen(
                                    kycFeatureFlowType = kycFeatureFlowType,
                                    fromScreen = fromScreen,
                                    childNavController = childNavController,
                                    shouldNotifyAfterOtpSuccess = shouldNotifyAfterOtpSuccess,
                                    nameForCreditReport = nameForCreditReport,
                                    panNumberForCreditReport = nameForCreditReport,
                                    lenderName = lenderName,
                                    validityInSeconds = it.data?.validityInSeconds.orZero().toLong(),
                                    resentOTPInSeconds = it.data?.resentOTPInSeconds.orZero().toLong(),
                                )
                            }
                        }
                    },
                    onError = { message, _ ->
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            apiStateCallback?.invoke(false, message)
                        }
                    }
                )
            } else {
                requestCreditReportOtpUseCase.requestCreditReportOtp(kycFeatureFlowType).collectUnwrapped(
                    onLoading = {
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            apiStateCallback?.invoke(true, null)
                        }
                    },
                    onSuccess = {
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            apiStateCallback?.invoke(false, null)
                            if (it.success) {
                                navigateToPanOtpScreen(
                                    kycFeatureFlowType = kycFeatureFlowType,
                                    fromScreen = fromScreen,
                                    childNavController = childNavController,
                                    shouldNotifyAfterOtpSuccess = shouldNotifyAfterOtpSuccess,
                                    nameForCreditReport = nameForCreditReport,
                                    panNumberForCreditReport = nameForCreditReport,
                                    lenderName = lenderName,
                                    validityInSeconds = it.data?.validityInSeconds.orZero(),
                                    resentOTPInSeconds = it.data?.resentOTPInSeconds.orZero()
                                )
                            }
                        }
                    },
                    onError = { message, _ ->
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            apiStateCallback?.invoke(false, message)
                        }
                    }
                )
            }
        }
    }

    override fun openCreditReportFetchedScreen(
        creditReportPan: CreditReportPAN?,
        fromScreen: String,
        childNavController: NavController,
        kycFeatureFlowType: KycFeatureFlowType,
        lenderName: String?
    ) {
        creditReportPan?.let {
            val args = CreditReportScreenArguments(
                it,
                false,
                LendingKycConstants.PanFlowType.MANUAL,
                isBackNavOrViewOnlyFlow = false,
                PanErrorScreenPrimaryButtonAction.YES_DETAILS_ARE_CORRECT,
                PanErrorScreenSecondaryButtonAction.NO_ENTER_DETAILS_MANUALLY,
                fromScreen = fromScreen,
                description = activity.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_following_pan_is_associated_with_your_credit_report.resourceId),
                isPanAadhaarMismatch = false,
                kycFeatureFlowType = kycFeatureFlowType,
                lenderName = lenderName
            )
            val encoded = encodeUrl(serializer.encodeToString(args))
            childNavController.navigate(
                Uri.parse("android-app://com.jar.app/pan-fetched/$encoded"),
                getNavOptions(shouldAnimate = true)
            )
        }
    }

    override fun openAadharVerificationFlow(
        kycScreenArgs: KYCScreenArgs,
        childNavController: NavController,
        apiCallback: ((Boolean, String?) -> Unit)?
    ) {
        val encoded = encodeUrl(serializer.encodeToString(kycScreenArgs))
        childNavController.navigate(
            Uri.parse("android-app://com.jar.app/kycOptionsFragment/$encoded"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openSelfieFlow(
        kycScreenArgs: KYCScreenArgs,
        childNavController: NavController,
        apiCallback: ((Boolean, String?) -> Unit)?
    ) {
        val encoded = encodeUrl(serializer.encodeToString(kycScreenArgs))
        childNavController.navigate(
            Uri.parse("android-app://com.jar.app/selfieCheck/$encoded"),
            getNavOptions(shouldAnimate = true)
        )
    }

    private fun navigate(flowType: String, response: KycProgressResponse) {
        if (response.kycVerified) {
            navController.navigate(
                Uri.parse("android-app://com.jar.app/lending-kyc-verified"),
                getNavOptions(shouldAnimate = true)
            )
        } else {
            if (response.kycProgress?.EMAIL == null) {
                navController.navigate(
                    Uri.parse("android-app://com.jar.app/lending-kyc-onboarding/$flowType"),
                    getNavOptions(shouldAnimate = true)
                )
            } else {
                navController.navigate(
                    Uri.parse("android-app://com.jar.app/lending-kyc-resume/$flowType")
                )
            }
        }
    }

    private fun navigateToPanOtpScreen(
        kycFeatureFlowType: KycFeatureFlowType,
        fromScreen: String,
        childNavController: NavController,
        shouldNotifyAfterOtpSuccess: Boolean,
        nameForCreditReport: String?,
        panNumberForCreditReport: String?,
        lenderName: String?,
        validityInSeconds: Long,
        resentOTPInSeconds: Long
    ) {
        val otpArgs = OtpSheetArguments(
            LendingKycConstants.LendingKycOtpVerificationFlowType.CREDIT_REPORT,
            expiresInTime = validityInSeconds,
            resendTime = resentOTPInSeconds,
            email = null,
            emailMessageId = null,
            fromScreen = fromScreen,
            kycFeatureFlowType = kycFeatureFlowType,
            shouldNotifyAfterOtpSuccess = shouldNotifyAfterOtpSuccess,
            nameForCreditReport = nameForCreditReport,
            panNumberForCreditReport = panNumberForCreditReport,
            lenderName = lenderName
        )
        val encoded = encodeUrl(serializer.encodeToString(otpArgs))
        childNavController.navigate(
            Uri.parse("android-app://com.jar.app/otp-verification/$encoded"),
            getNavOptions(shouldAnimate = true)
        )
    }
}