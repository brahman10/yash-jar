package com.jar.app.feature_round_off.impl.ui.round_off_settings.disable_round_off

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffDialogDisableRoundOffBinding
import com.jar.app.feature_round_off.impl.ui.round_off_settings.RoundOffSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class DisableRoundOffDialogFragment : BaseDialogFragment<FeatureRoundOffDialogDisableRoundOffBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffDialogDisableRoundOffBinding
        get() = FeatureRoundOffDialogDisableRoundOffBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)

    private val viewModel by viewModels<RoundOffSettingsViewModel>() { defaultViewModelProviderFactory }

    override fun setup() {
        setupUI()
        initClickListeners()
        observeLiveData()
        analyticsHandler.postEvent(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_DisableRoundOffPopUp)
    }

    private fun setupUI() {
        binding.lottieView.playLottieWithUrlAndExceptionHandling(requireContext(),"${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/sad-emoji.json")
        binding.clDisabled.isVisible = false
    }

    private fun initClickListeners() {
        binding.btnClose.setDebounceClickListener {
            dismissAllowingStateLoss()
        }

        binding.btnDisable.setDebounceClickListener {
            analyticsHandler.postEvent(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_Disable_DisableRoundOffPopUp)
            viewModel.updateRoundOffState(false)
        }

        binding.btnPause.setDebounceClickListener {
            analyticsHandler.postEvent(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Clicked_Pause_DisableRoundOffPopUp)
            dismissAllowingStateLoss()
            findNavController().getBackStackEntry(R.id.roundOffDetailsFragment).savedStateHandle[com.jar.app.feature_round_off.shared.util.RoundOffConstants.OPEN_PAUSE_ROUND_OFF_DIALOG] =
                true
        }
    }

    private fun observeLiveData() {
        viewModel.updateRoundOffStateLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                binding.clContent.slideToRevealNew(
                    viewToReveal = binding.clDisabled,
                    onAnimationEnd = {
                        binding.lottieView.playAnimation()
                        uiScope.launch {
                            analyticsHandler.postEvent(com.jar.app.feature_round_off.shared.util.RoundOffEventKey.Shown_Success_DisableRoundOffPopUp)
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
            onSuccessWithNullData = {
                dismissProgressBar()
            },
            onError = {
                dismissProgressBar()
            }
        )
    }
}