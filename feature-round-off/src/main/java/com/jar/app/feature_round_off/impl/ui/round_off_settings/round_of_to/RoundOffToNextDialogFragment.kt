package com.jar.app.feature_round_off.impl.ui.round_off_settings.round_of_to

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.feature_round_off.databinding.FeatureRoundOffDialogRoundOffToNextBinding
import com.jar.app.feature_round_off.impl.ui.round_off_settings.RoundOffSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class RoundOffToNextDialogFragment :
    BaseDialogFragment<FeatureRoundOffDialogRoundOffToNextBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffDialogRoundOffToNextBinding
        get() = FeatureRoundOffDialogRoundOffToNextBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)

    private val viewModel by viewModels<RoundOffSettingsViewModel> { defaultViewModelProviderFactory }

    private val navArg by navArgs<RoundOffToNextDialogFragmentArgs>()

    private var roundOffAmt = 0

    override fun setup() {
        setUpUI()
        initClickListeners()
        observeLiveData()
        analyticsHandler.postEvent(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_SetRoundOffPopUp)
    }

    private fun setUpUI() {
        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json"
        )

        val roundOffTo = RoundOffSettingsViewModel.RoundOff.valueOf(
            navArg.roundOffTo ?: RoundOffSettingsViewModel.RoundOff.NEAREST_TEN.name
        )
        when (roundOffTo) {
            RoundOffSettingsViewModel.RoundOff.NEAREST_TEN -> {
                selectRoundOff10()
            }
            RoundOffSettingsViewModel.RoundOff.NEAREST_FIVE -> {
                selectRoundOff5()
            }
        }
        viewModel.updateRoundOffValue(roundOffTo)
    }

    private fun observeLiveData() {
        viewModel.updateRoundOffValueLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                binding.clContent.slideToRevealNew(
                    viewToReveal = binding.clSuccess,
                    onAnimationEnd = {
                        binding.lottieView.playAnimation()
                        uiScope.launch {
                            analyticsHandler.postEvent(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_Success_RoundOffPopUp)
                            EventBus.getDefault().post(
                                com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent(
                                    it
                                )
                            )
                            delay(3000)
                            dismissAllowingStateLoss()
                        }
                    }
                )
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    private fun initClickListeners() {
        binding.tvCancel.setDebounceClickListener {
            analyticsHandler.postEvent(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_Cancel_RoundOffPopUp)
            dismiss()
        }

        binding.tvRoundOff5.setDebounceClickListener {
            selectRoundOff5()
        }

        binding.tvRoundOff10.setDebounceClickListener {
            selectRoundOff10()
        }

        binding.btnSave.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_SetRoundOff_RoundOffPopUp,
                mapOf(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.amount to roundOffAmt)
            )
            viewModel.updateUserRoundOffValue()
        }
    }

    private fun selectRoundOff10() {
        roundOffAmt = 10
        binding.tvRoundOff10.isSelected = true
        binding.tvRoundOff5.isSelected = false
        viewModel.updateRoundOffValue(RoundOffSettingsViewModel.RoundOff.NEAREST_TEN)
        toggleSetButton(navArg.roundOffTo != RoundOffSettingsViewModel.RoundOff.NEAREST_TEN.name)
    }

    private fun selectRoundOff5() {
        roundOffAmt = 5
        binding.tvRoundOff10.isSelected = false
        binding.tvRoundOff5.isSelected = true
        viewModel.updateRoundOffValue(RoundOffSettingsViewModel.RoundOff.NEAREST_FIVE)
        toggleSetButton(navArg.roundOffTo != RoundOffSettingsViewModel.RoundOff.NEAREST_FIVE.name)
    }

    private fun toggleSetButton(isEnabled: Boolean) {
        binding.btnSave.setDisabled(isEnabled.not())
    }
}