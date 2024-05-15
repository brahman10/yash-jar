package com.jar.app.feature_round_off.impl.ui.user_validation.disable

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.feature_round_off.NavigationRoundOffDirections
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffPauseOrDisableBottomSheetBinding
import com.jar.app.feature_round_off.shared.MR
import com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class PauseOrDisableRoundOffBottomSheet :
    BaseBottomSheetDialogFragment<FeatureRoundOffPauseOrDisableBottomSheetBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffPauseOrDisableBottomSheetBinding
        get() = FeatureRoundOffPauseOrDisableBottomSheetBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    @Inject
    lateinit var coreUiApi: CoreUiApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModel: PauseOrDisableRoundOffViewModel by viewModels()
    private val args: PauseOrDisableRoundOffBottomSheetArgs by navArgs()

    override fun setup() {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
        analyticsHandler.postEvent(
            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_StopScreen_RoundoffSettings, mapOf(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown,
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.PaymentType to args.paymentType
            )
        )
    }

    private fun setupListener() {
        binding.btnDisable.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_StopScreen_RoundoffSettings, mapOf(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Disable,
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.PaymentType to args.paymentType
                )
            )
            viewModel.disableRoundOff()
        }

        binding.btnPauseInstead.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_StopScreen_RoundoffSettings, mapOf(
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Action to com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Pause,
                    com.jar.app.feature_round_off.shared.util.RoundOffEventKey.PaymentType to args.paymentType
                )
            )
            navigateTo(
                NavigationRoundOffDirections.actionToPauseRoundOffBottomSheet(args.paymentType),
                popUpTo = R.id.pauseOrDisableRoundOffBottomSheet,
                inclusive = true
            )
        }

        binding.ivCross.setDebounceClickListener {
            dismiss()
        }
    }

    private fun observeLiveData() {
        viewModel.disableRoundOffLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                EventBus.getDefault().post(RefreshRoundOffStateEvent())
                coreUiApi.openGenericPostActionStatusFragment(
                    GenericPostActionStatusData(
                        postActionStatus = PostActionStatus.DISABLED.name,
                        header = getCustomString(MR.strings.feature_round_off_disabled_emoji),
                        description = getCustomString(MR.strings.feature_round_off_we_are_sorry_to_see_you_go),
                        descriptionColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                        descTextSize = 12f,
                        imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_disabled,
                        shouldShowTopProgress = true
                    )
                ) {
                    analyticsHandler.postEvent(
                        com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_StopConfirmation_RoundoffSettings, mapOf(
                            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Status to "Disabled",
                            com.jar.app.feature_round_off.shared.util.RoundOffEventKey.PaymentType to args.paymentType
                        )
                    )
                    findNavController().getBackStackEntry(R.id.roundOffDetailsFragment).savedStateHandle[com.jar.app.feature_round_off.shared.util.RoundOffConstants.DISABLE_ROUND_OFF] =
                        true
                    popBackStack(R.id.disableRoundOffBottomSheet, true)
                }
            },
            onError = { dismissProgressBar() }
        )
    }
}