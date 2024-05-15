package com.jar.app.feature_lending.impl.ui.eligibility.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.databinding.FragmentLendingEligibilityLoadingBinding
import com.jar.app.feature_lending.impl.domain.event.UpdateLendingStepsToolbarEvent
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class LendingEligibilityLoadingFragment : BaseFragment<FragmentLendingEligibilityLoadingBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val viewModelProvider by viewModels<LendingEligibilityLoadingViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingEligibilityLoadingBinding
        get() = FragmentLendingEligibilityLoadingBinding::inflate


    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //Do nothing, user will be automatically navigated
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(LendingEventKeyV2.Lending_RCashScreenShown)
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
        EventBus.getDefault().post(
            UpdateLendingStepsToolbarEvent(shouldShowSteps = false, LendingStep.KYC)
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        setLoadingUI()
        fetchEligibility()
        observeFlow()
        initClickListeners()
    }

    private fun fetchEligibility() {
        viewModel.fetchLendingEligibility()
    }

    private fun initClickListeners() {
        binding.tvBackToHome.setDebounceClickListener {
            goToHome()
        }

        binding.btnGoToHomeFailed.setDebounceClickListener {
            goToHome()
        }

        binding.btnRetry.setDebounceClickListener {
            setLoadingUI()
            fetchEligibility()
        }

        binding.btnContactUs.setDebounceClickListener {
            requireContext().openWhatsapp(remoteConfigApi.getWhatsappNumber())
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.preApprovedFlow.collect(
                    onSuccess = {
                        it?.let {
//                    navigateTo(
//                        LendingEligibilityLoadingFragmentDirections.actionLendingEligibilityLoadingFragmentToLendingEligibilitySuccessFragment(it),
//                        popUpTo = R.id.lendingEligibilityLoadingFragment,
//                        inclusive = true,
//                        shouldAnimate = true
//                    )
                        }
                    },
                    onError = { errorMessage, errorCode ->
                        if (errorCode == LendingConstants.ErrorCodesLending.LendingEligibility.RETRY_LIMIT_EXHAUSTED)
                            setErrorState()
                        else
                            setPendingState()
                    },
                )
            }
        }
    }

    private fun setErrorState() {
        binding.lottieView.clearAnimation()
        binding.lottieView.playLottieWithUrlAndExceptionHandling(requireContext(), LendingConstants.LottieUrls.GENERIC_ERROR)
        binding.clFailedCTA.isVisible = true
        binding.tvDoNotPressBack.isVisible = false
        binding.clPendingCTA.isVisible = false
        binding.tvTitle.text = getCustomString(MR.strings.feature_lending_enable_to_check_eligibility)
        binding.tvDescription.text = getCustomString(MR.strings.feature_lending_we_will_notify_when_we_get_update)
        binding.tvDescription.isVisible = true
    }

    private fun setPendingState() {
        binding.lottieView.clearAnimation()
        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            LendingConstants.LottieUrls.GENERIC_LOADING
        )
        binding.clPendingCTA.isVisible = true
        binding.tvDoNotPressBack.isVisible = false
        binding.clFailedCTA.isVisible = false
        binding.tvTitle.text = getCustomString(MR.strings.feature_lending_this_is_taking_longer_than_expected)
        binding.tvDescription.text = getCustomString(MR.strings.feature_lending_we_need_some_time_to_check_eligibility)
        binding.tvDescription.isVisible = true

    }

    private fun goToHome() {
        EventBus.getDefault().post(GoToHomeEvent("LOAN_ELIGIBILITY"))
    }

    private fun setLoadingUI() {
        binding.lottieView.clearAnimation()
        binding.lottieView.playLottieWithUrlAndExceptionHandling(requireContext(), LendingConstants.LottieUrls.ELIGIBILITY_LOADING)
        binding.clFailedCTA.isVisible = false
        binding.tvDoNotPressBack.isVisible = true
        binding.clPendingCTA.isVisible = false
        binding.tvTitle.text = getCustomString(MR.strings.feature_lending_finding_offer)
        binding.tvDescription.isVisible = false
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}