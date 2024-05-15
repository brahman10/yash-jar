package com.jar.app.feature_goal_based_saving.impl.ui.goalPending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_compose_ui.utils.generateSpannedFromHtmlString
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_goal_based_saving.databinding.LayoutGoalPendingScreenBinding
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SuperSharedViewModel
import com.jar.app.feature_goal_based_saving.shared.data.model.InProgressResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class GoalPendingFragment: BaseFragment<LayoutGoalPendingScreenBinding>() {
    private val args by navArgs<GoalPendingFragmentArgs>()
    private val viewModel: GoalPendingFragmentViewModel by viewModels { defaultViewModelProviderFactory }
    private val sharedViewModel by activityViewModels<SuperSharedViewModel> { defaultViewModelProviderFactory }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> LayoutGoalPendingScreenBinding
        get() = LayoutGoalPendingScreenBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.handleAction(GoalPendingFragmentActions.Init(args.data))
        observeState()
    }

    private fun observeState() {
        uiScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.state.collect() {
                        it.OnData?.let {
                            setupUI(it)
                            setClickListener()
                        }

                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.onClickOnPendingBanner?.collect() {
                        sharedViewModel
                            .handleActions(
                                GoalBasedSavingActions.NavigatetoDeeplink(it)
                            )
                    }
                }
            }
        }
    }

    private fun setClickListener() {
        with(binding) {

            rlPendingHeading.setDebounceClickListener {
                viewModel.handleAction(
                    GoalPendingFragmentActions.OnClickOnBanner
                )
            }
        }
    }

    private fun setupUI(inProgressResponse: InProgressResponse) {
        with(binding) {
            Glide.with(leftImageView).load(inProgressResponse.banner?.iconLink).into(leftImageView)
            pendingBarHeadingText.text = inProgressResponse.banner?.text

            firstTextView.text = inProgressResponse.header
            secondTextView.text = generateSpannedFromHtmlString(inProgressResponse.goalMessage)

            thirdTextView.text = inProgressResponse.bottomHeader
            fourthTextView.text = inProgressResponse.dailyAmount
            Glide.with(imageView).load(inProgressResponse.goalImage).into(imageView)
            fifthTextView.text = inProgressResponse.dailyAmountDesc
        }
    }

}