package com.jar.app.feature_lending_kyc.impl.ui.pan.report_fetched.loading

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.data.dto.getKycFeatureFlowType
import com.jar.app.core_base.data.dto.isFromP2POrLending
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycDialogCreditReportFetchSuccessBinding
import com.jar.app.feature_lending_kyc.shared.domain.arguments.CreditReportScreenArguments
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class CreditReportFetchSuccessDialog :
    BaseDialogFragment<FeatureLendingKycDialogCreditReportFetchSuccessBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycDialogCreditReportFetchSuccessBinding
        get() = FeatureLendingKycDialogCreditReportFetchSuccessBinding::inflate
    override val dialogConfig: DialogFragmentConfig
        get() = DEFAULT_CONFIG

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    companion object {
        const val CREDIT_REPORT_FETCHED_SCREEN = "Credit Report Fetched Screen"
    }

    private val args: CreditReportFetchSuccessDialogArgs by navArgs()

    override fun setup() {
        if (getKycFeatureFlowType(args.kycFeatureFlowType).isFromP2POrLending()) {
            binding.tvLookingReport.text =
                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_fetching_your_pan_card_details)
        }
        uiScope.launch {
            binding.loadingLottie.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                BaseConstants.CDN_BASE_URL + LendingKycConstants.LottieUrls.PAPER_STACK
            )
            delay(1500)
            binding.tvPleaseWait.isVisible = true
            binding.clLoadingDataContainer.slideToRevealNew(
                binding.clReportFetchedContainer,
                onAnimationEnd = {
                    uiScope.launch {
                        delay(1000)
                        analyticsHandler.postEvent(LendingKycEventKey.Shown_CreditReportFetchedScreen)
                        navigateToCreditReportFetchedScreen(
                            args.creditReportPan, args.jarVerifiedPAN
                        )
                    }
                }
            )
        }
    }

    private fun navigateToCreditReportFetchedScreen(
        creditReportPAN: CreditReportPAN?, jarVerifiedPAN: Boolean
    ) {
        val args = encodeUrl(serializer.encodeToString(
            CreditReportScreenArguments(
            creditReportPAN,
            jarVerifiedPAN,
            LendingKycConstants.PanFlowType.CREDIT_REPORT,
            isBackNavOrViewOnlyFlow = false,
            primaryAction = PanErrorScreenPrimaryButtonAction.YES_THIS_IS_MY_PAN,
            secondaryAction = PanErrorScreenSecondaryButtonAction.NO_THIS_IS_NOT_MY_PAN,
            fromScreen = CREDIT_REPORT_FETCHED_SCREEN,
            description = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_following_pan_is_associated_with_your_credit_report),
            kycFeatureFlowType = getKycFeatureFlowType(args.kycFeatureFlowType)
        )
        ))
        navigateTo(
            FeatureLendingKycStepsNavigationDirections.actionToCreditReportFetchedStep(args),
            shouldAnimate = true
        )
    }
}