package com.myjar.app.feature_exit_survey.impl.ui.questions

import androidx.recyclerview.widget.RecyclerView
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_exit_survey.shared.domain.model.Choice
import com.myjar.app.feature_exit_survey.databinding.FeatureExitSurveyLayoutBinding

class ExitSurveyQuestionViewHolder(
    private val binding: FeatureExitSurveyLayoutBinding,
    private val onOptionSelect: (reason: String, position: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun updateSelected(data: Choice) {
        data.isSelected = !data.isSelected
        binding.radioButton.isChecked = data.isSelected
    }

    fun bind(data: Choice, position: Int) {
        with(binding) {
            textView.text = data.text
            radioButton.isChecked = data.isSelected
            root.setDebounceClickListener {
                onOptionSelect.invoke(data.text.orEmpty(), position)
            }
            radioButton.setDebounceClickListener {
                onOptionSelect.invoke(data.text.orEmpty(), position)
            }
        }
    }
}