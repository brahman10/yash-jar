package com.jar.app.feature_goal_based_saving.impl.ui.goalSetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.databinding.GoalSettingFragmentBinding
import com.jar.app.feature_goal_based_saving.impl.ui.compose.GBSSettingsUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class GoalSettingFragment: BaseFragment<GoalSettingFragmentBinding>() {
    private val viewModel by viewModels<GoalSettingFragmentViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> GoalSettingFragmentBinding
        get() = GoalSettingFragmentBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.handleAction(GoalSettingFragmentActions.Init)
        setupView()
        setupClickListener()
        observeState()
    }

    private fun setupClickListener() {
        with(binding) {
            ivBack.setDebounceClickListener {
                popBackStack()
            }
        }
    }

    private fun observeState() {
        uiScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect { it ->
                    it.onData?.let {
                        binding.appBar.isVisible = true
                        binding.composView.setContent {
                            GBSSettingsUI(viewModel)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.loading.collect() {
                    if (!it) {
                        dismissProgressBar()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.onEndGoal.collect {
                    it?.let {
                        binding.appBar.isVisible = true
                        val direction = GoalSettingFragmentDirections.actionGoalSettingToEndGoal("",it)
                        navigateTo(direction, )
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.onTrackGoal.collect() {
                    it?.let {
                        popBackStack()
                        EventBus.getDefault().post(
                            HandleDeepLinkEvent(it)
                        )
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.onContactUs.collect() {
                    it?.let {
                        context?.openWhatsapp(
                            it,
                            getString(
                                com.jar.app.core_ui.R.string.buy_gold,
                            )
                        )
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.onClickOnPendingBanner.collect {
                    it?.let {
                        EventBus.getDefault().post(HandleDeepLinkEvent(it))

                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.onNavigateGoalCompletionScreen.collect {
                    it?.let {
                        binding.appBar.isVisible = true
                        val direction = GoalSettingFragmentDirections.actionGoalSettingToGoalSuccessFragment(it.first,it.second)
                        navigateTo(direction, popUpTo = R.id.goalSetting, inclusive = true)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.onBackButtonClick.collect() {
                    it?.let {
                        popBackStack()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.onNavigateGoalSetupScreen.collect {
                    it?.let {
                        popBackStack(R.id.goalSetting, true)
                        navigateTo(BaseConstants.InternalDeepLinks.SAVINGS_GOAL)
                    }
                }
            }
        }
    }

    private fun setupView() {
        with(binding) {
            ivBack.setDebounceClickListener {
                viewModel.handleAction(
                    GoalSettingFragmentActions.OnClickBack
                )
            }

        }
    }
}