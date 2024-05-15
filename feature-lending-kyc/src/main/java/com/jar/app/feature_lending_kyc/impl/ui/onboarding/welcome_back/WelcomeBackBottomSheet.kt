package com.jar.app.feature_lending_kyc.impl.ui.onboarding.welcome_back

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponseUnwrapped
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycWelcomeBackBottomsheetBinding
import com.jar.app.feature_lending_kyc.impl.ui.steps.LendingKycStepsViewModel
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class WelcomeBackBottomSheet :
    BaseBottomSheetDialogFragment<FeatureLendingKycWelcomeBackBottomsheetBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args: WelcomeBackBottomSheetArgs by navArgs()
    private val viewModel: LendingKycStepsViewModel by activityViewModels()

    companion object {
        const val CONTINUE_KYC = "Continue KYC"
        const val CROSS_BUTTON = "Cross button"
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycWelcomeBackBottomsheetBinding
        get() = FeatureLendingKycWelcomeBackBottomsheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun setup() {
        setupUi()
        setClickListener()
        observeLiveData()
    }

    private fun setupUi() {
        viewModel.fetchKycProgress(WeakReference(requireActivity()), true)
    }

    private fun setClickListener() {
        binding.btnContinueKyc.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_ContinueKYCBottomSheet,
                mapOf(
                    LendingKycEventKey.optionChosen to CONTINUE_KYC
                )
            )
            navigateTo(
                WelcomeBackBottomSheetDirections.actionToLendingKycStepsFragment(args.flowType)
            )
        }
        binding.ivCross.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_ContinueKYCBottomSheet,
                mapOf(
                    LendingKycEventKey.optionChosen to CROSS_BUTTON
                )
            )
            dismiss()
        }
    }

    private fun observeLiveData() {
        viewModel.kycStepsLiveData.observeNetworkResponseUnwrapped(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                binding.stepView.setSteps(it)
            }
        )

        viewModel.stepsRemainingLiveData.observe(viewLifecycleOwner) {
            uiScope.launch {
                binding.tvRemainingSteps.text =
                    getCustomStringFormatted(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_remaining_steps, it)
                analyticsHandler.postEvent(
                    LendingKycEventKey.Shown_ContinueKYCBottomSheet,
                    mapOf(
                        LendingKycEventKey.textDisplayed to binding.tvRemainingSteps.text
                    )
                )
            }
        }
    }
}