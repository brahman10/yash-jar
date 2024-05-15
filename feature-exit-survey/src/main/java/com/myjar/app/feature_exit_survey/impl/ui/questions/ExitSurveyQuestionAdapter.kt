package com.myjar.app.feature_exit_survey.impl.ui.questions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_exit_survey.shared.domain.model.Choice
import com.myjar.app.feature_exit_survey.R
import com.myjar.app.feature_exit_survey.databinding.FeatureExitSurveyLayoutBinding
import com.myjar.app.feature_exit_survey.databinding.FeatureExitSurveySelectQuestionLayoutWitjInputBinding

class ExitSurveyQuestionAdapter(
    private val list: MutableList<Choice>,
    private val onOptionSelect: (reason: String, position: Int) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val SELECTED = "selected"
        const val DESELECTED = "deselect"
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        when(viewType) {
            R.layout.feature_exit_survey_layout -> {
                val binding = FeatureExitSurveyLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ExitSurveyQuestionViewHolder(binding, onOptionSelect)
            }
            else -> {
                val binding = FeatureExitSurveySelectQuestionLayoutWitjInputBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ExitSurveyQuestionWithInputTextViewHolder(binding, onOptionSelect)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ExitSurveyQuestionViewHolder) {
            list.getOrNull(position)?.let { holder.bind(it, position) }
        } else if (holder is ExitSurveyQuestionWithInputTextViewHolder) {
            list.getOrNull(position)?.let { holder.bind(it, position) }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty().not()) {
            when(payloads.getOrNull(0)) {
                SELECTED, DESELECTED -> {
                    list.getOrNull(position)?.let {
                        if (holder is ExitSurveyQuestionViewHolder)
                            holder.updateSelected(it)
                        else if (holder is ExitSurveyQuestionWithInputTextViewHolder)
                            holder.updateSelected(it)
                    }
                }
                else -> {
                    super.onBindViewHolder(holder, position, payloads)
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(list[position].editable) {
            false -> {
                R.layout.feature_exit_survey_layout
            }
            else -> {
                R.layout.feature_exit_survey_select_question_layout_witj_input
            }
        }
    }
}