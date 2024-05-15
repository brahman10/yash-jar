package com.jar.app.weekly_magic_common.impl.ui.mystery_card_notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.RefreshWeeklyChallengeMetaEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_weekly_magic_common.shared.MR
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants
import com.jar.app.weekly_magic_common.databinding.DialogMysteryCardWonBinding
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class MysteryCardWonDialogFragment : BaseBottomSheetDialogFragment<DialogMysteryCardWonBinding>() {


    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<MysteryCardWonDialogFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DialogMysteryCardWonBinding
        get() = DialogMysteryCardWonBinding::inflate

    override val bottomSheetConfig = BottomSheetConfig(
        isHideable = false,
        shouldShowFullHeight = true,
        isCancellable = false,
        isDraggable = false
    )

    override fun setup() {
        setUpUI()
        setClickListeners()
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().postSticky(RefreshWeeklyChallengeMetaEvent())
    }

    private fun setUpUI() {
        binding.animViewMain.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.WEEKLY_CHALLENGE_NOTIFICATION_LOOP
        )
        binding.tvOnTransactionOf.text =
            getCustomStringFormatted(
                MR.strings.feature_weekly_magic_common_on_your_transaction_of,
                args.amount.orZero().toInt()
            )
        analyticsHandler.postEvent(
            WeeklyMagicConstants.AnalyticsKeys.Shown_RewardBottomSheet,
            mapOf(
                WeeklyMagicConstants.AnalyticsKeys.Parameters.scenario to args.transactionType
            )
        )
    }

    private fun setClickListeners() {
        binding.btnShowMe.setDebounceClickListener {
            analyticsHandler.postEvent(
                WeeklyMagicConstants.AnalyticsKeys.Clicked_Button_RewardBottomSheet,
                mapOf(
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.scenario to args.transactionType,
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.optionChosen to WeeklyMagicConstants.AnalyticsKeys.Values.Show_me
                )
            )
            dismiss()
            findNavController().currentBackStackEntry?.savedStateHandle
                ?.set(WeeklyMagicConstants.On_MYSTERY_CARD_WON_DIALOG_FINISHED, true)
        }
        binding.tvCheckLater.setDebounceClickListener {
            analyticsHandler.postEvent(
                WeeklyMagicConstants.AnalyticsKeys.Clicked_Button_RewardBottomSheet,
                mapOf(
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.optionChosen to WeeklyMagicConstants.AnalyticsKeys.Values.I_ll_check_later
                )
            )
            dismiss()
            findNavController().currentBackStackEntry?.savedStateHandle
                ?.set(WeeklyMagicConstants.On_MYSTERY_CARD_WON_DIALOG_FINISHED, false)
        }
    }
}