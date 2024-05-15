package com.jar.app.feature_lending_kyc.impl.ui.steps

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavDirections
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.DispatcherProvider
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.domain.model.*
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.impl.domain.model.KYCScreenArgs
import com.jar.app.feature_lending_kyc.shared.domain.model.*
import com.jar.app.feature_lending_kyc.impl.ui.aadhaar.confirmation.AadhaarConfirmationFragment
import com.jar.app.feature_lending_kyc.impl.ui.pan.error_screens.PanErrorStatesArguments
import com.jar.app.feature_lending_kyc.impl.ui.pan.report_not_fetched.CreditReportNotFetchedArguments
import com.jar.app.feature_lending_kyc.shared.MR
import com.jar.app.feature_lending_kyc.shared.domain.arguments.CreditReportScreenArguments
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarActionPromptArgs
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaar
import com.jar.app.feature_lending_kyc.shared.domain.model.ManualPanEntryScreenArguments
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LendingKycNavigationGenerator @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val prefs: PrefsApi,
    private val serializer: Serializer
) {

    companion object {
        const val CONTINUE_KYC_BOTTOM_SHEET = "Continue KYC BottomSheet"
    }

    suspend fun getKycCurrentStepNavigationDirection(
        contextRef: WeakReference<FragmentActivity>,
        kycProgressResponse: KycProgressResponse
    ): NavDirections =
        withContext(dispatcherProvider.default) {
            var navDirection: NavDirections =
                FeatureLendingKycStepsNavigationDirections.actionToEmailVerificationFragment(
                    null
                )
            //If response is empty that signifies that the lending kyc process is not yet started
            if (kycProgressResponse.kycProgress != null) {
                kycProgressResponse.kycProgress!!.EMAIL?.let {
                    navDirection = emailVerificationProgress(it)
                }
                kycProgressResponse.kycProgress!!.PAN?.let {
                    navDirection = panVerificationProgress(
                        it,
                        false,
                        contextRef
                    )
                }
                kycProgressResponse.kycProgress!!.AADHAAR?.let {
                    navDirection = aadhaarVerificationProgress(it, contextRef = contextRef)
                }
                kycProgressResponse.kycProgress!!.SELFIE?.let {
                    navDirection = selfieVerificationProgress(it, contextRef)
                }
            } else {
                navDirection =
                    FeatureLendingKycStepsNavigationDirections.actionToEmailVerificationFragment(
                        null
                    )
            }

            return@withContext navDirection
        }

    fun emailVerificationProgress(email: EMAIL?, isViewOnlyFlow: Boolean = false): NavDirections {
        return when (email?.status) {
            KycEmailAndAadhaarProgressStatus.VERIFIED.name -> {
                if (isViewOnlyFlow)
                    FeatureLendingKycStepsNavigationDirections.actionToEmailVerificationFragment(
                        email.email
                    )
                else
                    FeatureLendingKycStepsNavigationDirections.actionToPanVerificationFragment(
                        LendingKycConstants.PanFlowType.CREDIT_REPORT.name, 0, 0
                    )
            }
            KycEmailAndAadhaarProgressStatus.OTP_SENT.name, KycEmailAndAadhaarProgressStatus.EXPIRED.name, KycEmailAndAadhaarProgressStatus.FAILED.name, KycEmailAndAadhaarProgressStatus.OTP_VERIFIED.name -> {
                FeatureLendingKycStepsNavigationDirections.actionToEmailVerificationFragment(
                    email.email
                )
            }
            else -> FeatureLendingKycStepsNavigationDirections.actionToEmailVerificationFragment(
                null
            )
        }
    }

    fun panVerificationProgress(
        pan: PAN?,
        isViewOnlyFlow: Boolean = false,
        contextRef: WeakReference<FragmentActivity>,
    ): NavDirections {
        val context = contextRef.get()!!
        return when (pan?.status) {
            KycPANProgressStatus.VERIFIED.name -> {
                if (isViewOnlyFlow && pan.panNo != null) {
                    val args = encodeUrl(
                        serializer.encodeToString(
                            CreditReportScreenArguments(
                                CreditReportPAN(
                                    pan.panNo!!,
                                    pan.firstName.orEmpty(),
                                    pan.lastName.orEmpty(),
                                    pan.dob.orEmpty()
                                ),
                                pan.jarVerifiedPAN.orFalse(),
                                LendingKycConstants.PanFlowType.BACK_FLOW,
                                isBackNavOrViewOnlyFlow = true,
                                primaryAction = PanErrorScreenPrimaryButtonAction.NONE,
                                secondaryAction = PanErrorScreenSecondaryButtonAction.NONE,
                                fromScreen = CONTINUE_KYC_BOTTOM_SHEET,
                                description = context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_following_pan_is_associated_with_your_credit_report.resourceId)
                            )
                        )
                    )
                    FeatureLendingKycStepsNavigationDirections.actionToCreditReportFetchedStep(
                    args
                    )
                }else
                    FeatureLendingKycStepsNavigationDirections.actionToAadhaarCkycfetchFragment()
            }
            KycPANProgressStatus.OTP_VERIFIED.name -> {
                pan.panNo?.let { panNumber ->
                    //Credit Report was available so we redirected to credit report fetched Flow
                    val args = encodeUrl(serializer.encodeToString(
                        CreditReportScreenArguments(
                        CreditReportPAN(
                            panNumber,
                            pan.firstName.orEmpty(),
                            pan.lastName.orEmpty(),
                            pan.dob.orEmpty()
                        ),
                        pan.jarVerifiedPAN.orFalse(),
                        LendingKycConstants.PanFlowType.CREDIT_REPORT,
                        isBackNavOrViewOnlyFlow = false,
                        primaryAction = PanErrorScreenPrimaryButtonAction.YES_THIS_IS_MY_PAN,
                        secondaryAction = PanErrorScreenSecondaryButtonAction.NO_THIS_IS_NOT_MY_PAN,
                        fromScreen = CONTINUE_KYC_BOTTOM_SHEET,
                        description = context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_following_pan_is_associated_with_your_credit_report.resourceId)
                    )
                    ))
                    FeatureLendingKycStepsNavigationDirections.actionToCreditReportFetchedStep(
                        args
                    )
                } ?: kotlin.run {
                    //Credit Report wasn't available so we'll go to jarVerified or Manual PAN Flow
                    FeatureLendingKycStepsNavigationDirections.actionToCreditReportNotAvailableFragment(
                        CreditReportNotFetchedArguments(
                            title = context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_verification.resourceId),
                            description = context.getString(
                                if (pan.jarVerifiedPAN == true) com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_enter_pan_using_one_of_these_methods.resourceId else
                                    com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_enter_your_pan_details.resourceId
                            ),
                            assetUrl = BaseConstants.CDN_BASE_URL + if (pan.jarVerifiedPAN.orFalse()) LendingKycConstants.IllustrationUrls.PAN_PLACEHOLDER_URL else LendingKycConstants.IllustrationUrls.PAN_CARD_NOT_DETECTED_URL,
                            primaryAction = if (pan.jarVerifiedPAN.orFalse()) PanErrorScreenPrimaryButtonAction.USE_PAN_SAVED_WITH_JAR else PanErrorScreenPrimaryButtonAction.ENTER_PAN_MANUALLY,
                            secondaryAction = if (pan.jarVerifiedPAN.orFalse()) PanErrorScreenSecondaryButtonAction.ENTER_PAN_MANUALLY else PanErrorScreenSecondaryButtonAction.NONE,
                            fromScreen = CONTINUE_KYC_BOTTOM_SHEET,
                            jarVerifiedPAN = pan.jarVerifiedPAN.orFalse()
                        )
                    )
                }
            }
            KycPANProgressStatus.FAILED.name -> {
                val screenArgs = encodeUrl(
                    serializer.encodeToString(
                        ManualPanEntryScreenArguments(
                        CONTINUE_KYC_BOTTOM_SHEET, jarVerifiedPAN = pan.jarVerifiedPAN.orFalse()
                    )
                    ))
                FeatureLendingKycStepsNavigationDirections.actionToEnterPanManuallyStep(
                    screenArgs
                )
            }
            KycPANProgressStatus.OTP_SENT.name -> {
                FeatureLendingKycStepsNavigationDirections.actionToPanVerificationFragment(
                    LendingKycConstants.PanFlowType.CREDIT_REPORT.name,
                    pan.validityInSeconds.orZero(),
                    pan.resentOTPInSeconds.orZero()
                )
            }
            KycPANProgressStatus.RETRY_LIMIT_EXCEEDED.name -> {
                FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                    PanErrorStatesArguments(
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_entry_attempt_limit_exceeded.resourceId),
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_try_again_tomorrow.resourceId),
                        BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                        primaryAction = if (pan.jarVerifiedPAN.orFalse()) PanErrorScreenPrimaryButtonAction.USE_PAN_SAVED_WITH_JAR else PanErrorScreenPrimaryButtonAction.ENTER_PAN_MANUALLY,
                        secondaryAction = if (pan.jarVerifiedPAN.orFalse()) PanErrorScreenSecondaryButtonAction.ENTER_PAN_MANUALLY else PanErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                        jarVerifiedPAN = pan.jarVerifiedPAN,
                        contactMessage = context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_credit_report_otp_limit_exceeded.resourceId),
                        fromScreen = CONTINUE_KYC_BOTTOM_SHEET
                    )
                )
            }
            KycPANProgressStatus.RETRY_LIMIT_EXHAUSTED.name -> {
                FeatureLendingKycStepsNavigationDirections.actionToPanErrorStatesFragment(
                    PanErrorStatesArguments(
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_entry_attempt_limit_exhausted.resourceId),
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_contact_customer_support.resourceId),
                        BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                        primaryAction = if (pan.jarVerifiedPAN.orFalse()) PanErrorScreenPrimaryButtonAction.USE_PAN_SAVED_WITH_JAR else PanErrorScreenPrimaryButtonAction.ENTER_PAN_MANUALLY,
                        secondaryAction = if (pan.jarVerifiedPAN.orFalse()) PanErrorScreenSecondaryButtonAction.ENTER_PAN_MANUALLY else PanErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                        jarVerifiedPAN = pan.jarVerifiedPAN,
                        contactMessage = context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_credit_report_otp_limit_exhausted.resourceId),
                        fromScreen = CONTINUE_KYC_BOTTOM_SHEET
                    )
                )
            }
            else -> {
                FeatureLendingKycStepsNavigationDirections.actionToPanVerificationFragment(
                    LendingKycConstants.PanFlowType.CREDIT_REPORT.name, 0, 0
                )
            }
        }
    }

    fun aadhaarVerificationProgress(
        aadhaar: AADHAAR?,
        isViewOnlyFlow: Boolean = false,
        contextRef: WeakReference<FragmentActivity>,
    ): NavDirections {
        val context = contextRef.get()!!
        return when (aadhaar?.status) {
            KycEmailAndAadhaarProgressStatus.CKYC_AADHAAR.name -> {
                aadhaar.aadhaarNo?.let {
                    FeatureLendingKycStepsNavigationDirections.actionToAadhaarConfirmationFragment(
                        KycAadhaar(aadhaar.aadhaarNo, aadhaar.dob, aadhaar.name),
                        "",
                        AadhaarConfirmationFragment.FLOW_CKYC
                    )
                } ?: run {
                    FeatureLendingKycStepsNavigationDirections.actionToAadhaarManualEntryConsentPromptFragment(
                        LendingKycEventKey.PARAM_KYC_Landing_Screen
                    )
                }
            }
            KycEmailAndAadhaarProgressStatus.VERIFIED.name -> {
                if (isViewOnlyFlow) {
                    FeatureLendingKycStepsNavigationDirections.actionToVerifiedAadhaarFragment(
                        KycAadhaar(aadhaar.aadhaarNo, aadhaar.dob, aadhaar.name)
                    )
                } else {
                    val encoded = encodeUrl(serializer.encodeToString(KYCScreenArgs()))
                    FeatureLendingKycStepsNavigationDirections.actionToSelfieCheckFragment(encoded)
                }
            }
            KycEmailAndAadhaarProgressStatus.OTP_VERIFIED.name -> {
                FeatureLendingKycStepsNavigationDirections.actionToAadhaarActionPromptFragment(
                    AadhaarActionPromptArgs(
                        BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_uh_oh_exclamation.resourceId),
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_pan_aadhaar_details_dont_match.resourceId),
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_re_enter_aadhaar_details.resourceId),
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_re_enter_pan_details.resourceId),
                        AadhaarErrorScreenPrimaryButtonAction.EDIT_AADHAAR,
                        AadhaarErrorScreenSecondaryButtonAction.EDIT_PAN,
                        isPanAadhaarMismatch = true
                    )
                )
            }
            KycEmailAndAadhaarProgressStatus.OTP_SENT.name, KycEmailAndAadhaarProgressStatus.EXPIRED.name, KycEmailAndAadhaarProgressStatus.FAILED.name -> {
                FeatureLendingKycStepsNavigationDirections.actionToAadhaarManualEntryConsentPromptFragment(
                    LendingKycEventKey.PARAM_KYC_Landing_Screen
                )
            }
            else -> {
                if (aadhaar?.aadhaarNo != null && aadhaar.name != null) {
                    FeatureLendingKycStepsNavigationDirections.actionToAadhaarConfirmationFragment(
                        KycAadhaar(aadhaar.aadhaarNo, aadhaar.dob, aadhaar.name),
                        "",
                        AadhaarConfirmationFragment.FLOW_AADHAAR_UPLOAD
                    )
                } else {
                    FeatureLendingKycStepsNavigationDirections.actionToAadhaarManualEntryConsentPromptFragment(
                        LendingKycEventKey.PARAM_KYC_Landing_Screen
                    )
                }
            }
        }
    }

    fun selfieVerificationProgress(
        selfie: SELFIE?,
        contextRef: WeakReference<FragmentActivity>
    ): NavDirections {
        val context = contextRef.get()!!
        return when (selfie?.status) {
            KycSelfieProgressStatus.FAILED.name -> {
                val encoded = encodeUrl(serializer.encodeToString(KYCScreenArgs()))
                FeatureLendingKycStepsNavigationDirections.actionToSelfieCheckFragment(encoded)
            }
            KycSelfieProgressStatus.RETRY_LIMIT_EXCEEDED.name -> {
                FeatureLendingKycStepsNavigationDirections.actionToAadhaarActionPromptFragment(
                    AadhaarActionPromptArgs(
                        BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verification_attempt_limit_exceeded.resourceId),
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_try_again_tomorrow.resourceId),
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_go_home.resourceId),
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support.resourceId),
                        AadhaarErrorScreenPrimaryButtonAction.GO_HOME,
                        AadhaarErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                        contactMessage = context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_selfie_limit_exceeded.resourceId)
                    )
                )
            }
            KycSelfieProgressStatus.RETRY_LIMIT_EXHAUSTED.name -> {
                FeatureLendingKycStepsNavigationDirections.actionToAadhaarActionPromptFragment(
                    AadhaarActionPromptArgs(
                        BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.GENERIC_ERROR,
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verification_attempt_limit_exhausted.resourceId),
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_contact_customer_support.resourceId),
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_go_home.resourceId),
                        context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support.resourceId),
                        AadhaarErrorScreenPrimaryButtonAction.GO_HOME,
                        AadhaarErrorScreenSecondaryButtonAction.CONTACT_SUPPORT,
                        contactMessage = context.getString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_selfie_limit_exhausted.resourceId)
                    )
                )
            }
            else -> {
                val encoded = encodeUrl(serializer.encodeToString(KYCScreenArgs()))
                FeatureLendingKycStepsNavigationDirections.actionToSelfieCheckFragment(encoded)
            }
        }
    }

}