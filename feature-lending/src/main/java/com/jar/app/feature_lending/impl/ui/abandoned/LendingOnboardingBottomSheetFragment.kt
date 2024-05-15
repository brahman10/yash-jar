package com.jar.app.feature_lending.impl.ui.abandoned

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.LendingOnboardingToKycEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.databinding.FragmentLendingOnboardingBottomSheetBinding
import com.jar.app.feature_lending.impl.ui.common.LendingProgressViewModelAndroid
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class LendingOnboardingBottomSheetFragment : BaseBottomSheetDialogFragment<FragmentLendingOnboardingBottomSheetBinding>() {

    private val lendingProgressViewModelProvider by viewModels<LendingProgressViewModelAndroid> { defaultViewModelProviderFactory }
    private val lendingProgressViewModel by lazy {
        lendingProgressViewModelProvider.getInstance()
    }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingOnboardingBottomSheetBinding
        get() = FragmentLendingOnboardingBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isCancellable = true, isDraggable = false)

    private val args by navArgs<LendingOnboardingFragmentArgs>()

    override fun setup() {
        observeLiveData()
        initClickListeners()
        getData()
    }

    private fun initClickListeners() {
        binding.ivCross.setDebounceClickListener {
            dismissAllowingStateLoss()
        }

        binding.btnContinue.setDebounceClickListener {
            navigateAhead()
        }
    }

    private fun navigateAhead() {
        if (lendingProgressViewModel.loanApplications?.kycJourney?.kycVerified.orFalse()) {
//            navigateTo(
//                LendingOnboardingBottomSheetFragmentDirections.actionLendingOnboardingBottomSheetFragmentToLendingStepsFragment(
//                    flowType = args.flowType
//                )
//            )
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

    private fun getData() {
        lendingProgressViewModel.fetchLendingProgress()
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                lendingProgressViewModel.loanApplicationsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
//                binding.tvWelcomeBackDescription.setText(it.getDescriptionAccordingToStatus())
//                lendingProgressViewModel.fetchLendingStepList(it, activityRef, R.id.lendingOnboardingBottomSheetFragment, false)
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        dismissAllowingStateLoss()
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
}