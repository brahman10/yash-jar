package com.jar.app.feature_goal_based_saving.impl.ui.qna.qeustions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_goal_based_saving.databinding.QnaLayoutBinding

class QuestionsAdapter(
    val questions: MutableList<Question> = mutableListOf(),
    val onSelect: (String, Int) -> Unit
) : RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = QnaLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding, onSelect)
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position], position)
    }

    inner class QuestionViewHolder(
        private val binding: QnaLayoutBinding,
        private val onSelect: (String, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(qnAResponse: Question, position: Int) {
            with(binding) {
                radioButton.isChecked = qnAResponse.selected
                radioButton.setOnClickListener{
                    onSelect.invoke(qnAResponse.question, bindingAdapterPosition)
                }
                binding.root.setOnClickListener {
                    onSelect.invoke(qnAResponse.question, bindingAdapterPosition)
                }

                textView.text = qnAResponse.question
            }
        }
    }
}


data class Question(val question: String, var selected: Boolean = false)