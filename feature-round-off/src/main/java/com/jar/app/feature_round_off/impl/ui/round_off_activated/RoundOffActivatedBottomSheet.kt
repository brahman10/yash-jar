package com.jar.app.feature_round_off.impl.ui.round_off_activated

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffActivatedBottomSheetBinding
import com.jar.app.feature_round_off.shared.MR
import com.jar.app.feature_round_off.shared.util.RoundOffConstants
import com.jar.app.feature_round_off.shared.util.RoundOffEventKey
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class RoundOffActivatedBottomSheet :
    BaseBottomSheetDialogFragment<FeatureRoundOffActivatedBottomSheetBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffActivatedBottomSheetBinding
        get() = FeatureRoundOffActivatedBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    companion object {
        const val RoundOffActivatedBottomSheet = "RoundOffActivatedBottomSheet"
    }

    private val args: RoundOffActivatedBottomSheetArgs by navArgs()

    private val viewModel: RoundOffActivatedViewModel by viewModels()

    override fun setup() {
        viewModel.enableManualRoundOff()
        setupUI()
        setupListener()
    }

    private fun setupUI() {
        binding.tvRsValue.text =
            getCustomStringFormatted(MR.strings.feature_round_off_currency_int_x, args.roundOffAmount.toInt())
        analyticsHandler.postEvent(
            RoundOffEventKey.RoundOff_ManualPaymentActivated_Screen,
            mapOf(
                RoundOffEventKey.Action to RoundOffEventKey.Shown,
                RoundOffEventKey.IsSpendsDetected to args.isSpendsDetected,
            )
        )
        if (args.isSpendsDetected) {
            binding.tvTitle.text =
                getCustomString(MR.strings.feature_round_off_yay_manual_round_off_activated)
            binding.tvSubtitle.text =
                getCustomString(MR.strings.feature_round_off_your_round_off_will_be_calculated_and_displayed)
            binding.tvThisIsHowRoundOffCardLooks.text =
                getCustomString(MR.strings.feature_round_off_this_is_what_a_detected_round_off_card_looks)
        } else {
            binding.tvTitle.text =
                getCustomString(MR.strings.feature_round_off_manual_round_off_is_active_now)
            binding.tvSubtitle.text =
                getCustomString(MR.strings.feature_round_off_your_roundoffs_will_be_calculated)
            binding.tvThisIsHowRoundOffCardLooks.text =
                getCustomString(MR.strings.feature_round_off_you_can_find_them_on_spends_detected_card)
        }
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + RoundOffConstants.Illustration.ROUND_OFF_ACTIVATED)
            .into(binding.ivHeaderIllustration)
    }

    private fun setupListener() {
        binding.btnOkayGotIt.setDebounceClickListener {
            EventBus.getDefault().post(GoToHomeEvent(RoundOffActivatedBottomSheet))
            analyticsHandler.postEvent(
                RoundOffEventKey.RoundOff_ManualPaymentActivated_Screen,
                mapOf(
                    RoundOffEventKey.Action to RoundOffEventKey.OkayClicked,
                    RoundOffEventKey.IsSpendsDetected to args.isSpendsDetected,
                )
            )
        }
        binding.btnClose.setDebounceClickListener {
            dismiss()
            analyticsHandler.postEvent(
                RoundOffEventKey.RoundOff_ManualPaymentActivated_Screen,
                mapOf(
                    RoundOffEventKey.Action to RoundOffEventKey.CrossClicked,
                    RoundOffEventKey.IsSpendsDetected to args.isSpendsDetected,
                )
            )
        }
    }
}