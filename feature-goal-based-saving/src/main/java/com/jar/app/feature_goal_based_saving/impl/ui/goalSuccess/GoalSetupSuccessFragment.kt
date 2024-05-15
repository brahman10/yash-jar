package com.jar.app.feature_goal_based_saving.impl.ui.goalSuccess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.RefreshGoalBasedSavingEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_goal_based_saving.databinding.GoalSetupSuccessFragmentBinding
import com.jar.app.feature_goal_based_saving.impl.ui.compose.GoalCompletionScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class GoalSetupSuccessFragment: BaseFragment<GoalSetupSuccessFragmentBinding>() {
    private val args: GoalSetupSuccessFragmentArgs by navArgs()

    private val viewModel: GoalSetupSuccessFragmentViewModel by viewModels { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> GoalSetupSuccessFragmentBinding
        get() = GoalSetupSuccessFragmentBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.handleActions(GoalSetupSuccessFragmentActions.Init(args.data, args.goalId))
        EventBus.getDefault().post(
            RefreshGoalBasedSavingEvent()
        )
        setUpView()
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    viewModel.loading.collect {
                        if (it) {
                            showProgressBar()
                        } else {
                            dismissProgressBar()
                        }
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.onContinue.collect {
                        it?.let {
                            EventBus.getDefault().post(HandleDeepLinkEvent(it))
                        }
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.onWithDraw.collect {
                        it?.let {
                            EventBus.getDefault().post(HandleDeepLinkEvent(it))
                        }
                    }
                }
            }
        }
    }

    private fun setUpView() {
        binding.composeView.setContent {
            GoalCompletionScreen(viewModel)
        }
        binding.appBar.isVisible = args.isShowAppBar
        binding.ivBack.setDebounceClickListener {
            popBackStack()
        }
    }
}