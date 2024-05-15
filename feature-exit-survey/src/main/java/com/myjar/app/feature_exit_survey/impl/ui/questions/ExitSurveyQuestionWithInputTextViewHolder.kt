package com.myjar.app.feature_exit_survey.impl.ui.questions

import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_exit_survey.shared.domain.model.Choice
import com.myjar.app.feature_exit_survey.databinding.FeatureExitSurveySelectQuestionLayoutWitjInputBinding

class ExitSurveyQuestionWithInputTextViewHolder  (private val binding: FeatureExitSurveySelectQuestionLayoutWitjInputBinding,
private val onOptionSelect: (reason: String, position: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun updateSelected(data: Choice) {
        data.isSelected = !data.isSelected
        binding.radioButton.isChecked = data.isSelected
        binding.etReason.isVisible = data.isSelected
        if (data.isSelected) {
            binding.etReason.setText("")
        }
    }

    fun bind(data: Choice, position: Int) {
        with(binding) {
            textView.text = data.text
            radioButton.isChecked = data.isSelected
            binding.etReason.isVisible = data.isSelected
            binding.etReason.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    data.otherOptionText = s.toString()
                    onOptionSelect.invoke(binding.etReason.text.toString(), position)
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
            root.setDebounceClickListener {
                onOptionSelect.invoke(binding.etReason.text.toString(), position)
            }
            radioButton.setDebounceClickListener {
                onOptionSelect.invoke(binding.etReason.text.toString(), position)
            }
        }
    }
}