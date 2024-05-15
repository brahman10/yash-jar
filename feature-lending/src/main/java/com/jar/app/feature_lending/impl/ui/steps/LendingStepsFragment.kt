package com.jar.app.feature_lending.impl.ui.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.util.observeNetworkResponseUnwrapped
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLendingStepsBinding
import com.jar.app.feature_lending.impl.domain.event.UpdateLendingStepsToolbarEvent
import com.jar.app.feature_lending.impl.ui.common.LendingViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class LendingStepsFragment : BaseFragment<FragmentLendingStepsBinding>(){

    private val args by navArgs<LendingStepsFragmentArgs>()

    private lateinit var navController: NavController

    private val viewModel by activityViewModels<LendingViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingStepsBinding
        get() = FragmentLendingStepsBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(com.jar.app.core_ui.R.color.color_322C48)
        getData()
        observeLiveData()
        setupUI()
        setupListeners()
    }

    private fun setupListeners() {

        binding.lendingToolbar.setBackButtonClickListener {
            if (shouldPopToExitLending()) {
                popBackStack()
            } else {
                performBackNavigation()
            }
        }
    }

    private fun setupUI() {
        binding.lendingToolbar.isVisible = false
        val nestedNavHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = nestedNavHostFragment.navController
        navController.graph =
            nestedNavHostFragment.navController.navInflater.inflate(R.navigation.lending_steps_navigation)
    }

    private fun openBackPressedDialog() {
//        navigateTo(
//            LendingStepsFragmentDirections.actionToLendingBackPressedFragment(
//                title = getCustomString(MR.strings.feature_lending_are_you_sure_you_want_to_leave),
//                des = getCustomString(MR.strings.feature_lending_almost_done_checking_eligibility)
//            )
//        )
    }

    private fun getData() {
        viewModel.fetchLendingProgress()
    }

    private fun observeLiveData() {
        val weakRef: WeakReference<View> = WeakReference(binding.root)
        val activityRef = WeakReference(requireActivity())

        viewModel.loanApplicationsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                if (viewModel.shouldSuppressRedirection.not()) {
                   it?.let {
                       viewModel.fetchLendingStepList(
                           it,
                           activityRef,
                           navController.currentBackStackEntry?.destination?.id,
                           false
                       )
                       viewModel.fetchLendingStepNavigation(
                           it,
                           navController.currentBackStackEntry?.destination?.id,
                           args.flowType
                       )
                   }
                }
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.lendingStepLiveData.observe(this) {
            binding.lendingToolbar.addSteps(it)
        }

        viewModel.lendingNavDirectionsLiveData.observeNetworkResponseUnwrapped(
            viewLifecycleOwner,
            weakRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                navController.navigate(it, getNavOptions(true))
            },
            onSuccessWithNullData = {
                dismissProgressBar()
            },
            onError = { _,_ ->
                dismissProgressBar()
            }
        )

        viewModel.lendingBackNavDirectionsLiveData.observe(viewLifecycleOwner) {
//            it.let {
//                if (it.navDirections == LendingStepsFragmentDirections.actionToLendingBackPressedFragment(
//                        title = getCustomString(MR.strings.feature_lending_are_you_sure_you_want_to_leave),
//                        des = getCustomString(MR.strings.feature_lending_almost_done_checking_eligibility)
//                    )
//                ) {
//                    openBackPressedDialog()
//                } else {
//                    viewModel.loanApplications?.let { loanApplications ->
//                        viewModel.fetchLendingStepList(loanApplications, activityRef, navController.currentBackStackEntry?.destination?.id, it.isBackNavigation)
//                    }
//                    if (shouldPopToExitLending() && it.isBackNavigation) {
//                        popBackStack()
//                    } else {
//                        navController.navigate(
//                            it.navDirections,
//                            getNavOptions(
//                                true,
//                                showBackwardNavigationAnimation = it.isBackNavigation,
//                                popUpToId = R.id.dummyScreenFragment,
//                                inclusive = true
//                            )
//                        )
//                    }
//                }
//            }
        }
    }

    //If Kyc View Only Fragment then pop back stack to exit Lending Steps Fragment
    private fun shouldPopToExitLending() =
        navController.currentDestination?.id == R.id.lendingKycViewOnlyFragment
                || navController.currentDestination?.id == R.id.loanReasonFragment
                || navController.currentDestination?.id == R.id.loanFinalDetailsFragment

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onUpdateLendingStepsToolbarEvent(updateLendingStepsToolbarEvent: UpdateLendingStepsToolbarEvent) {
        EventBus.getDefault().removeStickyEvent(updateLendingStepsToolbarEvent)
        lifecycleScope.launchWhenCreated {
            binding.lendingToolbar.isVisible = updateLendingStepsToolbarEvent.shouldShowSteps
            setStatusBarColor(if (updateLendingStepsToolbarEvent.shouldShowSteps) com.jar.app.core_ui.R.color.color_322C48 else com.jar.app.core_ui.R.color.bgColor)
            binding.lendingToolbar.setTitle(getCustomString(updateLendingStepsToolbarEvent.step.titleRes))
//            binding.lendingToolbar.setStepCurrentPosition(updateLendingStepsToolbarEvent.step.stepNumber)
        }
    }

    private fun performBackNavigation() {
        viewModel.toolbarBackNavigation(
            navController.currentBackStackEntry,
            WeakReference(requireActivity()),
            flowType = args.flowType
        )
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}