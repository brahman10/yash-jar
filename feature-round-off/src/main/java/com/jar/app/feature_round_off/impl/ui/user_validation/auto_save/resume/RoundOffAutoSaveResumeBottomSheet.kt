package com.jar.app.feature_round_off.impl.ui.user_validation.auto_save.resume

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_round_off.NavigationRoundOffDirections
import com.jar.app.feature_round_off.databinding.FeatureRoundOffResumeAutoSaveBottomSheetBinding
import com.jar.app.feature_round_off.shared.MR
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class RoundOffAutoSaveResumeBottomSheet :
    BaseBottomSheetDialogFragment<FeatureRoundOffResumeAutoSaveBottomSheetBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffResumeAutoSaveBottomSheetBinding
        get() = FeatureRoundOffResumeAutoSaveBottomSheetBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args: RoundOffAutoSaveResumeBottomSheetArgs by navArgs()
    private val viewModel: RoundOffAutoSaveResumeViewModel by viewModels()

    override fun setup() {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
//        viewModel.isAutoPayResetRequired(args.roundOffAmount)
        analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_AutomaticPayemnt_ManualRoundoffSettingsScreen,
            mapOf(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.MandateRequired to (args.roundOffAmount >= args.mandateAmount)
            )
        )
        binding.tvDescription.text = HtmlCompat.fromHtml(
            getCustomStringFormatted(
                MR.strings.feature_round_off_amount_rs_x,
                if (args.roundOffAmount == 0f) args.mandateAmount.toInt() else args.roundOffAmount.toInt()
            ), HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun setupListener() {
        binding.btnConfirm.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_AutomaticPayemnt_ManualRoundoffSettingsScreen,
                mapOf(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Confirm,
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.MandateRequired to (args.roundOffAmount >= args.mandateAmount)
                )
            )
                navigateTo(
                    NavigationRoundOffDirections.actionToPreRoundOffAutopaySetupFragment()
                )
        }

        binding.btnCancel.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_AutomaticPayemnt_ManualRoundoffSettingsScreen,
                mapOf(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Cancel,
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.MandateRequired to (args.roundOffAmount >= args.mandateAmount)
                )
            )
            dismiss()
        }

        binding.ivCross.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_AutomaticPayemnt_ManualRoundoffSettingsScreen,
                mapOf(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.CrossClicked,
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.MandateRequired to (args.roundOffAmount >= args.mandateAmount)
                )
            )
            dismiss()
        }
    }

    private fun observeLiveData() {
        viewModel.isAutoPayResetRequiredLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
            },
            onError = { dismissProgressBar() }
        )
    }
}