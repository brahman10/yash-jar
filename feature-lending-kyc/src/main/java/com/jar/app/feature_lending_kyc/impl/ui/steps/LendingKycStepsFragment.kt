package com.jar.app.feature_lending_kyc.impl.ui.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.core_ui.util.observeNetworkResponseUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_lending_kyc.FeatureLendingKycNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycStepsFragmentBinding
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class LendingKycStepsFragment : BaseFragment<FeatureLendingKycStepsFragmentBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private lateinit var navController: NavController

    private val args: LendingKycStepsFragmentArgs by navArgs()
    private val viewModel: LendingKycStepsViewModel by activityViewModels()
    private var activityRef: WeakReference<FragmentActivity>? = null
    private var nestedNavHostFragment: NavHostFragment? = null

    companion object {
        private const val BACK_ARROW = "Back arrow"
        private const val CROSS_BUTTON = "Cross Button"
        private const val LendingKycStepsFragment = "LendingKycStepsFragment"
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                popBackStack(R.id.lendingKycStepsFragment, true)
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycStepsFragmentBinding
        get() = FeatureLendingKycStepsFragmentBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(com.jar.app.core_ui.R.color.color_322C48)
        setupUI()
        setupListeners()
        observeLiveData()
//        observeBackStack()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        activityRef = WeakReference(requireActivity())
        viewModel.flowType = args.flowType
        viewModel.fetchKycProgress(activityRef!!, false)

        nestedNavHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = nestedNavHostFragment!!.navController
        navController.graph =
            nestedNavHostFragment!!.navController.navInflater.inflate(R.navigation.feature_lending_kyc_steps_navigation)
    }

    private fun setupListeners() {
        binding.kycToolbar.setCloseButtonClickListener {
            viewModel.notifyToolbarInteraction(CROSS_BUTTON)
            navigateTo(
                LendingKycStepsFragmentDirections.actionLendingKycStepsFragmentToExitLendingFlowBottomSheet(
                    viewModel.stepsRemaining
                ),
                true
            )
        }
        binding.kycToolbar.setBackButtonClickListener {
            viewModel.notifyToolbarInteraction(BACK_ARROW)
            viewModel.toolbarBackNavigation(
                navController.currentBackStackEntry,
                WeakReference(requireActivity())
            )
        }
    }

    private fun observeLiveData() {
        viewModel.kycStepsLiveData.observeNetworkResponseUnwrapped(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                binding.kycToolbar.addSteps(it)
            }
        )
        viewModel.kycNavDirectionsLiveData.observeNetworkResponseUnwrapped(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                navController.navigate(
                    it, getNavOptions(
                        true,
                        popUpToId = R.id.dummyScreenFragment,
                        inclusive = true
                    )
                )
            },
            onError = { _, _ ->
                dismissProgressBar()
            }
        )

        viewModel.kycBackNavDirectionsLiveData.observe(viewLifecycleOwner) {
            it.let {
                if (it.navDirections == FeatureLendingKycNavigationDirections.actionToExitLendingFlowBottomSheet(
                        1
                    )
                ) {
                    navigateTo(
                        LendingKycStepsFragmentDirections.actionLendingKycStepsFragmentToExitLendingFlowBottomSheet(
                            viewModel.stepsRemaining
                        ),
                        true,
                        popUpTo = R.id.dummyScreenFragment,
                        inclusive = true
                    )
                } else {
                    navController.navigate(
                        it.navDirections,
                        getNavOptions(
                            true,
                            showBackwardNavigationAnimation = it.isBackNavigation,
                            popUpToId = R.id.dummyScreenFragment,
                            inclusive = true
                        )
                    )
                }
            }
        }
    }

    private fun observeBackStack() {
        uiScope.countDownTimer(
            100000000,
            1000,
            onInterval = {
                val queue = navController
                    .backQueue
                    .map {
                        it.destination
                    }
                    .filterNot {
                        it is NavGraph
                    }
                    .joinToString(" > ") {
                        it.displayName.split('/')[1]
                    }

                Timber.d("lending-kyc navController.graph : $queue")

            },
            onFinished = {}
        )
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        setStatusBarColor(com.jar.app.core_ui.R.color.bgColor)
        super.onDestroyView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onStepToolbarEvent(toolbarStepsVisibilityEvent: ToolbarStepsVisibilityEvent) {
        EventBus.getDefault().removeStickyEvent(toolbarStepsVisibilityEvent)
        lifecycleScope.launchWhenCreated {
            binding.kycToolbar.isVisible = toolbarStepsVisibilityEvent.shouldShowSteps
            setStatusBarColor(if (toolbarStepsVisibilityEvent.shouldShowSteps) com.jar.app.core_ui.R.color.color_322C48 else com.jar.app.core_ui.R.color.bgColor)
            binding.kycToolbar.setTitle(getString(toolbarStepsVisibilityEvent.step.titleRes))
            binding.kycToolbar.setStepCurrentPosition(toolbarStepsVisibilityEvent.step.stepNumber)
            binding.kycToolbar.showToolbarSeparator(toolbarStepsVisibilityEvent.shouldShowToolbarSeparator)
        }
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