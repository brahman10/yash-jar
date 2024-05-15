package com.jar.app.feature_lending.impl.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.databinding.CellQuestionAnswerBinding
import com.jar.app.feature_lending.shared.domain.model.v2.QuestionAnswer

internal class QuestionAnswerAdapter : ListAdapter<QuestionAnswer, QuestionAnswerAdapter.KeyValueViewHolder>(
    DIFF_UTIL
) {
    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<QuestionAnswer>() {
            override fun areItemsTheSame(oldItem: QuestionAnswer, newItem: QuestionAnswer): Boolean {
                return oldItem.question == newItem.question
            }

            override fun areContentsTheSame(oldItem: QuestionAnswer, newItem: QuestionAnswer): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = KeyValueViewHolder(
        CellQuestionAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: KeyValueViewHolder, position: Int) {
        getItem(position)?.let { holder.setData(it) }
    }

    internal class KeyValueViewHolder(private val binding: CellQuestionAnswerBinding) : BaseViewHolder(binding.root) {

        fun setData(keyValueData: QuestionAnswer) {
            binding.tvQuestion.text = keyValueData.question
            binding.tvAnswer.text = keyValueData.answer
        }
    }
}