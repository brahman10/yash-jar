package com.jar.app.feature_lending.impl.ui.eligibility.rejected

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.base.util.replaceListTagFromHtml
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending.databinding.CellEligibilityFaqBinding
import com.jar.app.feature_lending.shared.domain.model.v2.QuestionAnswer
import timber.log.Timber

internal class LendingEligibilityFaqAdapter(private val onClick: (title: String) -> Unit) :
    ListAdapter<QuestionAnswer, LendingEligibilityFaqAdapter.LendingEligibilityFaqViewHolder>(
        DIFF_UTIL
    ) {
    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<QuestionAnswer>() {
            override fun areItemsTheSame(
                oldItem: QuestionAnswer,
                newItem: QuestionAnswer
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: QuestionAnswer,
                newItem: QuestionAnswer
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    internal inner class LendingEligibilityFaqViewHolder(
        private val binding: CellEligibilityFaqBinding,
        onClick: (title: String) -> Unit
    ) : BaseViewHolder(binding.root) {

        private var qna: QuestionAnswer? = null

        init {
            binding.root.setDebounceClickListener(ctaDebounceTimeInMillis = 200L){
                onClick.invoke(qna?.question.orEmpty())
                binding.ivExpand.animate()
                    .rotation(if (binding.expandableLayout.isExpanded) 0f else 180f).start()
                binding.expandableLayout.toggle()
            }
        }

        fun bind(data: QuestionAnswer) {
            this.qna = data
            binding.ivExpand.rotation = if (binding.expandableLayout.isExpanded) 180f else 0f
            binding.tvQuestion.text = data.question
            binding.tvAnswer.text = HtmlCompat.fromHtml(
                data.answer.orEmpty().replaceListTagFromHtml(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            ).trim()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LendingEligibilityFaqViewHolder {
        val binding = CellEligibilityFaqBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LendingEligibilityFaqViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: LendingEligibilityFaqViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}