package com.jar.app.core_ui.expandable_rv

import com.jar.app.core_base.domain.model.ExpandableDataItem
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.CardContainerExpandableLayoutBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder

internal class CardExpandableFaqViewHolder(
    private val binding: CardContainerExpandableLayoutBinding,
    private val onItemClick: (Int) -> Unit
) : BaseViewHolder(binding.root) {

    fun bind(faq: ExpandableDataItem.CardHeaderIsExpandedDataType) {
        binding.expandableContentContainer.isExpanded = faq.isExpanded
        binding.questionTv.text = faq.question
        binding.tvAnswer.text = faq.answer

        binding.root.setDebounceClickListener {
            onItemClick?.invoke(absoluteAdapterPosition)
            binding.expandableHeaderIv.animate()
                .rotation(if (binding.expandableContentContainer.isExpanded) 0f else 180f).start()
            binding.expandableContentContainer.toggle()
        }
    }
}