package com.jar.app.feature_lending.impl.ui.abandoned

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.LendingOnboardingToKycEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLendingOnboardingBinding
import com.jar.app.feature_lending.shared.domain.model.temp.LoanApplications
import com.jar.app.feature_lending.impl.ui.common.LendingProgressViewModelAndroid
import com.jar.app.feature_lending.impl.ui.common.LendingViewModel
import com.jar.app.feature_lending.shared.domain.LendingEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR
import com.jar.internal.library.jar_core_network.api.util.collect

@AndroidEntryPoint
internal class LendingOnboardingFragment : BaseFragment<FragmentLendingOnboardingBinding>(){

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<LendingOnboardingFragmentArgs>()

    private val lendingProgressViewModelProvider by viewModels<LendingProgressViewModelAndroid> { defaultViewModelProviderFactory }
    private val lendingProgressViewModel by lazy {
        lendingProgressViewModelProvider.getInstance()
    }


    private val lendingViewModel by activityViewModels<LendingViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingOnboardingBinding
        get() = FragmentLendingOnboardingBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        getData()
        setupUI()
        initClickListeners()
        observeLiveData()
    }

    private fun initClickListeners() {
        binding.btnGetStarted.setDebounceClickListener {
            lendingProgressViewModel.loanApplications?.kycJourney?.kycVerified?.let {
                if (it) {
                    val stepName = lendingViewModel.currentStep?.let { getCustomString(it.titleResId) }
                        ?: BaseConstants.EMPTY
                    analyticsHandler.postEvent(
                        LendingEventKey.OnClick_ReadyCash_Continue,
                        mapOf(
                            LendingEventKey.entryPoint to args.flowType,
                            LendingEventKey.step to stepName
                        )
                    )
                } else {
                    analyticsHandler.postEvent(
                        LendingEventKey.OnClick_ReadyCash_StartKyc,
                        mapOf(
                            LendingEventKey.entryPoint to args.flowType
                        )
                    )
                }
            } ?: kotlin.run {
                analyticsHandler.postEvent(
                    LendingEventKey.OnClick_ReadyCash_StartKyc,
                    mapOf(
                        LendingEventKey.entryPoint to args.flowType
                    )
                )
            }
            navigateAhead()
        }
    }

    private fun navigateAhead() {
        if (lendingProgressViewModel.loanApplications?.kycJourney?.kycVerified.orFalse()) {
            navigateTo(
                LendingOnboardingFragmentDirections.actionLendingOnboardingFragmentToLendingStepsFragment(
                    flowType = args.flowType
                )
            )
        } else {
            lendingProgressViewModel.loanApplications?.kycJourney?.let {
                EventBus.getDefault().post(
                    LendingOnboardingToKycEvent(
                        flowType = BaseConstants.LendingKycFromScreen.LENDING_ONBOARDING,
                        progressResponse = it
                    )
                )
            } ?: kotlin.run {
                EventBus.getDefault().post(
                    LendingOnboardingToKycEvent(
                        flowType = BaseConstants.LendingKycFromScreen.LENDING_ONBOARDING,
                        progressResponse = null
                    )
                )
            }
        }
    }

    private fun setupUI() {
        setStatusBarColor(com.jar.app.core_ui.R.color.bgColor)
        setupToolbar()
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.feature_lending_jar_ready_cash)

        binding.toolbar.separator.isVisible = true

        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false

        binding.toolbar.btnBack.setDebounceClickListener {
            lendingProgressViewModel.loanApplications?.kycJourney?.kycVerified?.let {
                if (it) {
                    val stepName = lendingViewModel.currentStep?.let { getCustomString(it.titleResId) }
                        ?: BaseConstants.EMPTY
                    analyticsHandler.postEvent(
                        LendingEventKey.OnClick_ReadyCash_Continue_Back,
                        mapOf(
                            LendingEventKey.entryPoint to args.flowType,
                            LendingEventKey.step to stepName
                        )
                    )
                } else {
                    analyticsHandler.postEvent(
                        LendingEventKey.OnClick_ReadyCash_StartKyc_Back,
                        mapOf(
                            LendingEventKey.entryPoint to args.flowType
                        )
                    )
                }
            } ?: kotlin.run {
                analyticsHandler.postEvent(
                    LendingEventKey.OnClick_ReadyCash_StartKyc_Back,
                    mapOf(
                        LendingEventKey.entryPoint to args.flowType
                    )
                )
            }
            popBackStack()
        }
    }

    private fun getData() {
        lendingProgressViewModel.fetchLendingProgress()
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root)
        val activityRef: WeakReference<FragmentActivity> = WeakReference(requireActivity())

        lendingViewModel.networkStateLiveData.observe(viewLifecycleOwner) {
            binding.toolbar.clNetworkContainer.isSelected = it
            binding.toolbar.tvInternetConnectionText.text =
                if (it) getString(com.jar.app.core_ui.R.string.core_ui_we_are_back_online) else getString(
                    com.jar.app.core_ui.R.string.core_ui_no_internet_available_please_try_again)
            binding.toolbar.tvInternetConnectionText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (it) com.jar.app.core_ui.R.drawable.ic_wifi_on else com.jar.app.core_ui.R.drawable.ic_wifi_off, 0, 0, 0
            )
            if (it) {
                if (binding.toolbar.networkExpandableLayout.isExpanded) {
                    uiScope.launch {
                        delay(500)
                        binding.toolbar.networkExpandableLayout.collapse(true)
                    }
                }
            } else {
                binding.toolbar.networkExpandableLayout.expand(true)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                lendingProgressViewModel.loanApplicationsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setUIText(it)
                        }
                    },
                    onError = {errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                lendingProgressViewModel.lendingStepFlow.collect {
                    binding.stepView.setSteps(it)
                }
            }
        }
    }

    private fun setUIText(loanApplications: LoanApplications) {
        if (loanApplications.kycJourney?.kycVerified.orFalse()) {
            binding.tvLendingTitle.text = getCustomString(MR.strings.feature_lending_you_are_kyc_verified)
            binding.tvLendingTitle.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.feature_lending_ic_100_secure, 0,
            )
            binding.tvLendingDesc.text = getCustomString(loanApplications.getDescriptionAccordingToStatus())
            binding.tvLendingDesc.isVisible = true
            binding.btnGetStarted.setText(getCustomString(MR.strings.feature_lending_continue))
        } else {
            binding.tvLendingDesc.isVisible = false
            loanApplications.kycJourney?.let {
                binding.tvLendingTitle.text = getCustomString(MR.strings.feature_lending_complete_your_kyc)
                binding.btnGetStarted.setText(getCustomString(MR.strings.feature_lending_continue_kyc))
                binding.tvLendingTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, 0, 0,
                )
            } ?: kotlin.run {
                binding.tvLendingTitle.text = getCustomString(MR.strings.feature_lending_simple_steps_to_get_loan)
                binding.btnGetStarted.setText(getCustomString(MR.strings.feature_lending_start_kyc))
                binding.tvLendingTitle.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, 0, 0,
                )
            }
        }
    }


}