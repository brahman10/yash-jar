package com.jar.app.feature_lending_kyc.impl.ui.exit_bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycBottomSheetExitLendingFlowBinding
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class ExitLendingFlowBottomSheet :
    BaseBottomSheetDialogFragment<FeatureLendingKycBottomSheetExitLendingFlowBinding>() {
    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<ExitLendingFlowBottomSheetArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycBottomSheetExitLendingFlowBinding
        get() = FeatureLendingKycBottomSheetExitLendingFlowBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun setup() {
        setupUI()
        setClickListener()
    }

    private fun setupUI() {
        binding.tvJustnStepsRemaining.text = resources.getQuantityString(
            com.jar.app.feature_lending_kyc.shared.MR.plurals.feature_lending_kyc_you_can_finish_kyc_in_just_d_steps.resourceId,
            args.stepRemainingNumber,
            args.stepRemainingNumber
        )
        analyticsHandler.postEvent(
            LendingKycEventKey.Shown_ExitBottomSheet,
            mapOf(
                LendingKycEventKey.textDisplayed to binding.tvJustnStepsRemaining.text
            )
        )
    }

    private fun setClickListener() {
        binding.btnDoLater.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_ExitBottomSheet,
                mapOf(
                    LendingKycEventKey.textDisplayed to binding.tvJustnStepsRemaining.text,
                    LendingKycEventKey.optionChosen to getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_i_ll_do_it_later)
                )
            )
            popBackStack(R.id.lendingKycStepsFragment, true)
        }
        binding.tvCancel.setDebounceClickListener {
            analyticsHandler.postEvent(
                LendingKycEventKey.Clicked_Button_ExitBottomSheet,
                mapOf(
                    LendingKycEventKey.textDisplayed to binding.tvJustnStepsRemaining.text,
                    LendingKycEventKey.optionChosen to getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_i_ll_do_it_now)
                )
            )
            dismiss()
        }
    }
}