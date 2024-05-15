package com.myjar.app.feature_exit_survey.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.SubmittedExitSurveyEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_exit_survey.shared.domain.model.Choice
import com.myjar.app.feature_exit_survey.databinding.ExitSurveyFragmentLayoutBinding
import com.myjar.app.feature_exit_survey.impl.ui.questions.ExitSurveyQuestionAdapter
import com.myjar.app.feature_exit_survey.impl.ui.questions.ExitSurveyQuestionAdapter.Companion.DESELECTED
import com.myjar.app.feature_exit_survey.impl.ui.questions.ExitSurveyQuestionAdapter.Companion.SELECTED
import com.myjar.app.feature_exit_survey.impl.util.openDialerWithPhoneNumber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class ExitSurveyFragment: BaseBottomSheetDialogFragment<ExitSurveyFragmentLayoutBinding>() {
    private val viewModel: ExitSurveyFragmentViewModel by viewModels { defaultViewModelProviderFactory }
    private lateinit var questionsAdapter: ExitSurveyQuestionAdapter
    private var currentlySelected = -1
    private val choicesList = mutableListOf<Choice>()
    private val args by navArgs<ExitSurveyFragmentArgs>()
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ExitSurveyFragmentLayoutBinding
        get() = ExitSurveyFragmentLayoutBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG.copy(
            isCancellable = false,
            isDraggable = false
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }


    override fun setup() {
        viewModel.handleAction(ExitSurveyFragmentAction.Init(args.surveyFor))
        setupView()
        setUpObserver()
    }

    private fun setUpObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.exitSurveyResponse.collect { it ->
                    binding.tvHeading.text = it?.question
                    if (it?.button2Cta != null) {
                        binding.btnNeedHelp.isVisible = true
                        binding.btnNeedHelp.setText(it.button2Cta?.text.orEmpty())
                        binding.btnNeedHelp.setDebounceClickListener { _ ->
                            dismissAllowingStateLoss()
                            viewModel.handleAction(
                                ExitSurveyFragmentAction.OnClickOnHelpCta
                            )
                            it.button2Cta?.deeplink?.let { it1 ->
                                requireActivity().openDialerWithPhoneNumber(
                                    it1
                                )
                            }
                        }
                    } else {
                        binding.btnNeedHelp.isVisible = false
                    }
                    it?.choices?.let {
                        choicesList.clear()
                        choicesList.addAll(it as MutableList<Choice>)
                        questionsAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.dismissBottomSheet.collect {
                    if (it) {
                        dismissNow()
                        EventBus.getDefault().post(SubmittedExitSurveyEvent())
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.loading.collect {
                }
            }
        }
    }

    private fun setupView() {
        with(binding) {
            questionsAdapter = ExitSurveyQuestionAdapter(choicesList) { _, position ->
                if (currentlySelected != position) {
                    // Deselect the previously selected item
                    if (currentlySelected != -1) {
                        questionsAdapter.notifyItemChanged(currentlySelected, DESELECTED)
                    }
                    // Select the new item
                    currentlySelected = position
                    questionsAdapter.notifyItemChanged(currentlySelected, SELECTED)
                }
                if (currentlySelected != -1) {
                    if (choicesList[currentlySelected].editable == false || (choicesList[currentlySelected].editable ==true && choicesList[currentlySelected].otherOptionText.isNullOrEmpty().not())) {
                        binding.btnSubmit.alpha = 1f
                        binding.btnSubmit.isEnabled = true
                    } else {
                        binding.btnSubmit.alpha = 0.5f
                        binding.btnSubmit.isEnabled = false
                    }
                }
            }
            rvQuestions.adapter= questionsAdapter
            rvQuestions.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            ivClose.setDebounceClickListener {
                dismiss()
            }

            btnSubmit.setDebounceClickListener {
                if (currentlySelected != -1) {
                    if (choicesList[currentlySelected].editable == false)
                        viewModel.handleAction(ExitSurveyFragmentAction.SubmitResponse(choicesList[currentlySelected].text))
                    else
                        viewModel.handleAction(ExitSurveyFragmentAction.SubmitResponse(choicesList[currentlySelected].otherOptionText.orEmpty()))
                }
            }
        }
    }
}