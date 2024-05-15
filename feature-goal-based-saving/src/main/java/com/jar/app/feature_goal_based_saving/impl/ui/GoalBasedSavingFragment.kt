package com.jar.app.feature_goal_based_saving.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.databinding.FragmentGoalBasedSavingBinding
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.ui.confirmScreen.ConfirmGoalBasedSavingFragmentDirections
import com.jar.app.feature_goal_based_saving.impl.ui.defaultLoadingScreen.DefaultLoadingScreenDirections
import com.jar.app.feature_goal_based_saving.impl.ui.userEntry.UserEntryFragmentDirections
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SuperSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.utils.TransactionFlow
import com.jar.app.feature_goal_based_saving.shared.data.model.ProgressStatus
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class GoalBasedSavingFragment : BaseFragment<FragmentGoalBasedSavingBinding>() {

    @Inject
    protected lateinit var serializer: Serializer
    private var navigationController: NavController? = null
    private val viewModel by viewModels<GoalBasedSavingFragmentViewModel> { defaultViewModelProviderFactory  }
    private val sharedViewModel by activityViewModels<SuperSharedViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoalBasedSavingBinding
        get() = FragmentGoalBasedSavingBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        if (sharedViewModel.state.value.isCallHomeFeedApi.get()) {
            viewModel.handleActions(GoalBasedSavingFragmentActions.Init)
        }
        setupView()
        observeState()
        observeGoalBasedSavingFragmentState()
        setupListeners()
        observeTransactionState()
    }

    private fun observeTransactionState() {
        viewLifecycleOwner.lifecycleScope.launch {
            TransactionFlow.transactionFlow.collect() {
                if (it.roundOffAmount != -1f && it.autoInvestStatus == MandatePaymentProgressStatus.SUCCESS) {
                    viewModel.updateRecurringAmount(it.roundOffAmount)
                }
                navigateToTranactionSuccessScreen(it.goalId)
            }
        }
    }

    private fun observeGoalBasedSavingFragmentState() {
        uiScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.loading.collect() {
                    if (it) {
                        showProgressBar()
                    } else {
                        dismissProgressBar()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data.collect {
                it?.let {
                    val goalState = ProgressStatus.fromString(it.progressStatus)
                    val currentDestination = navigationController?.currentDestination?.id
                    when(goalState) {
                        ProgressStatus.ACTIVE -> {
                            if (currentDestination == R.id.manageGoalFragment)
                                return@let
                            if (currentDestination == R.id.goalTransactionScreen2)
                                return@let
                            val data = serializer.encodeToString(it)
                            val direction = DefaultLoadingScreenDirections.actionGoalBasedSavingFragmentToManageGoalFragment(data)
                            sharedViewModel.handleActions(
                                GoalBasedSavingActions.NavigateWithDirection(direction)
                            )
                        }
                        ProgressStatus.IN_PROGRESS -> {
                            val data = serializer.encodeToString(it.inProgressResponse)
                            val direction = DefaultLoadingScreenDirections.actionIntroFragmentToGoalPendingFragment(data)
                            sharedViewModel.handleActions(
                                GoalBasedSavingActions.NavigateWithDirection(direction)
                            )
                        }
                        ProgressStatus.SETUP -> {
                            val direction = DefaultLoadingScreenDirections.actionIntroFragmentToIntroFragment()
                            sharedViewModel.handleActions(
                                GoalBasedSavingActions.NavigateWithDirection(direction)
                            )
                        }
                        ProgressStatus.COMPLETED -> {
                            if (it.goalCompletedResponse?.showEndState == true) {
                                val data = serializer.encodeToString(it.goalCompletedResponse?.endStateResponse)
                                val direction = DefaultLoadingScreenDirections.actionIntroFragmentToGoalSuccessFragment(
                                    data,
                                    it.goalId!!
                                )
                                sharedViewModel.handleActions(
                                    GoalBasedSavingActions.NavigateWithDirection(direction)
                                )
                            } else {
                                val data = serializer.encodeToString(it)
                                val direction = DefaultLoadingScreenDirections.actionGoalBasedSavingFragmentToManageGoalFragment(data)
                                sharedViewModel.handleActions(
                                    GoalBasedSavingActions.NavigateWithDirection(direction)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupView() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        navigationController = navHostFragment.navController
        navigationController?.graph =
            navHostFragment.navController.navInflater.inflate(R.navigation.goal_based_saving_v2)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                sharedViewModel.navigateTo.collect {
                    it?.let {
                        try {
                            navigationController?.navigate(it)
                        } catch (_: Exception) {}
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                sharedViewModel.navigateWithDirection.collect() {
                    it?.let {
                        try {
                            navigationController?.navigate(it)
                        } catch (_: Exception) { }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.navigateToPaymentSuccessScreen.collect {
                it?.let {
                    navigateToTranactionSuccessScreen(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                sharedViewModel.navigateToDeepLink.collect() {
                    it?.let {
                        EventBus.getDefault().post(HandleDeepLinkEvent(it))
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                sharedViewModel.onPopPayment.collect {
                    if (it == true) {
                        popBackStack()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    sharedViewModel.popBackStack.collect() {
                        it?.let {
                            popBackStack()
                        }
                    }
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    sharedViewModel.hideAppBar.collect() {
                        binding.appBar.isVisible = it != true
                    }
                }
            }
        }
    }

    private fun navigateToTranactionSuccessScreen(goalId: String) {
        val currentDestination = navigationController?.currentDestination?.id
        var direction: NavDirections? = null
        when(currentDestination) {
            R.id.defaultLoadingFragment -> {
                direction = DefaultLoadingScreenDirections.actionIntroFragmentToGoalTransactionScreen2(goalId)
            }
            R.id.userEntryFragment -> {
                direction = UserEntryFragmentDirections.actionUserEntryFragmentToGoalTransactionScreen2(goalId)
            }
            R.id.confirmGoalBasedSavingFragment -> {
                direction = ConfirmGoalBasedSavingFragmentDirections.actionConfirmGoalBasedSavingFragmentToGoalTransactionScreen2(goalId)
            }
        }
        navigateTo(
            "android-app://com.jar.app/goalPaymentStatus/${goalId}",
            popUpTo = currentDestination,
            inclusive = true
        )
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            when(navigationController?.currentDestination?.id) {
                R.id.confirmGoalBasedSavingFragment -> {
                    viewModel.handleActions(
                        GoalBasedSavingFragmentActions.OnBackIconClicked(
                            "Mandate Screen"
                        )
                    )
                }
                R.id.manageGoalFragment -> {
                    viewModel.handleActions(
                        GoalBasedSavingFragmentActions.OnBackIconClicked(
                            "Progress Screen"
                        )
                    )
                }
                R.id.goalSuccessFragment -> {
                    viewModel.handleActions(
                        GoalBasedSavingFragmentActions.OnBackIconClicked(
                            "Goal Status Screen"
                        )
                    )
                }
                R.id.goalSetting -> {
                    viewModel.handleActions(
                        GoalBasedSavingFragmentActions.OnBackIconClicked(
                            "Goal Status Screen"
                        )
                    )
                }
                R.id.enterUserAmountAndDuration -> {
                    viewModel.handleActions(
                        GoalBasedSavingFragmentActions.OnBackIconClicked(
                            "Goal amount and duration screen"
                        )
                    )
                }
            }
            requireActivity().onBackPressed()
        }
    }

}