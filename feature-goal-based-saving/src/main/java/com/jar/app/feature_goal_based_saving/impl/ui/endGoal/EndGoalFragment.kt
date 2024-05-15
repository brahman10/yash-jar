package com.jar.app.feature_goal_based_saving.impl.ui.endGoal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.RefreshGoalBasedSavingEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_goal_based_saving.databinding.EndGoalFragmentLayoutBinding
import com.jar.app.feature_goal_based_saving.shared.data.model.EndScreenResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class EndGoalFragment: BaseBottomSheetDialogFragment<EndGoalFragmentLayoutBinding>() {
    private val args by navArgs<EndGoalFragmentArgs>()
    private val viewModel by viewModels<EndGoalFragmentViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> EndGoalFragmentLayoutBinding
        get() = EndGoalFragmentLayoutBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        viewModel.handleAction(EndGoalFragmentAction.Init)
        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect() {
                    when(it) {
                        EndGoalFragmentState.Loading -> {
                            showProgressBar()
                        }
                        is EndGoalFragmentState.OnData -> {
                            setupView(it.data)
                            dismissProgressBar()
                        }

                        EndGoalFragmentState.OnClickOnContinue -> {
                            popBackStack()
                        }
                        EndGoalFragmentState.OnClickOnEndGoal -> {
                            EventBus.getDefault().post(
                                RefreshGoalBasedSavingEvent()
                            )
                        }

                        EndGoalFragmentState.OnClickOnClose -> {
                            popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun setupView(data: EndScreenResponse) {
        with(binding) {
            tvHeading.text = data.header
            Glide.with(endGoalIcon).load(data.image).into(endGoalIcon)
            leftButton.setText(data.continueButton?.text ?: "")
            rightButton.setText(data.endGoalButton?.text ?: "")

            ivClose.setDebounceClickListener {
                viewModel.handleAction(
                    EndGoalFragmentAction.OnClickOnContinueGoal
                )
            }

            leftButton.setDebounceClickListener {
                viewModel.handleAction(
                    EndGoalFragmentAction.OnClickOnContinueGoal
                )
            }

            rightButton.setDebounceClickListener {
                viewModel.handleAction(
                    EndGoalFragmentAction.OnClickOnEndGoal
                )
                val direction = EndGoalFragmentDirections.actionEndGoalToQnaScreen("{}", args.goalId)
                navigateTo(direction)
            }
        }
    }
}