package com.jar.app.feature_goal_based_saving.impl.ui.qna

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.base.data.event.RefreshGoalBasedSavingEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.databinding.QnaFragmentLayoutBinding
import com.jar.app.feature_goal_based_saving.impl.ui.qna.qeustions.Question
import com.jar.app.feature_goal_based_saving.impl.ui.qna.qeustions.QuestionsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class QnAFragment: BaseBottomSheetDialogFragment<QnaFragmentLayoutBinding>() {
    private val args by navArgs<QnAFragmentArgs>()
    private val viewModel by viewModels<QnAFragmentViewModel> { defaultViewModelProviderFactory }
    private var questionAdapter: QuestionsAdapter? = null
    private var currentlySelectedQuestionIndex = -1
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> QnaFragmentLayoutBinding
        get() = QnaFragmentLayoutBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        setupView()
        viewModel.handleActions(
            QnAFragmentActions.Init(args.goalId, args.goalEndResponse)
        )
        observeSate()
    }

    private fun observeSate() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect() {
                    it.selectedMessage?.let {
                        val selectedIndex = it.second
                        binding.btnSubmit.isEnabled = selectedIndex != -1
                        binding.btnSubmit.alpha = 1f
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.onLoading.collect {
                    if (it == true) {
                        showProgressBar()
                    } else {
                        dismissProgressBar()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.onData?.collect {
                    it?.let {
                        binding.tvHelpUsImprove.text = it.message
                        binding.tvQuestion.text = it.question

                        val newList = it.options?.map {
                            Question(
                                it,
                                false
                            )
                        }
                        questionAdapter?.apply {
                            newList?.let { it1 -> questions.addAll(it1) }
                        }
                        questionAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.            launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.onClose.collect {
                    it?.let {
                        popBackStack()
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.onNavigateToEndGoalScreen.collect {
                    it?.let {
                        EventBus.getDefault().post(
                            RefreshGoalBasedSavingEvent()
                        )
                        EventBus.getDefault().post(RefreshDailySavingEvent())
                        val direction = QnAFragmentDirections.actionQnaScreenToGoalSuccessFragment(it.endStateResponse, it.goalId )
                        navigateTo(
                            direction,
                            popUpTo = R.id.goalSetting,
                            inclusive = true
                        )
                    }
                }

            }
        }
    }

    private fun setupView() {
        with(binding) {
            questionAdapter = QuestionsAdapter(onSelect = {question, position ->
                if (currentlySelectedQuestionIndex == -1) {
                    currentlySelectedQuestionIndex = position
                    questionAdapter?.questions?.get(position)?.selected = true
                    questionAdapter?.notifyItemChanged(position)
                } else {
                    questionAdapter?.questions?.get(currentlySelectedQuestionIndex)?.selected = false
                    questionAdapter?.notifyItemChanged(currentlySelectedQuestionIndex)
                    questionAdapter?.questions?.get(position)?.selected = true
                    questionAdapter?.notifyItemChanged(position)
                    currentlySelectedQuestionIndex = position
                }
                viewModel.handleActions(
                    QnAFragmentActions.OnOptionSelected(question, position)
                )
            })
            rvQuestions.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = questionAdapter
            }
            btnSubmit.isEnabled = false
            btnSubmit.alpha = 0.5f
            btnSubmit.setDebounceClickListener {
                viewModel.handleActions(
                    QnAFragmentActions.OnClickOnSubmit
                )
            }
            ivClose.setDebounceClickListener {
                viewModel.handleActions(
                    QnAFragmentActions.OnClickOnClose
                )
            }
        }
    }
}