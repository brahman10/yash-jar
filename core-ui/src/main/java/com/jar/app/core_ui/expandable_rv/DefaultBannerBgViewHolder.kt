package com.jar.app.core_ui.expandable_rv

import com.jar.app.core_base.domain.model.ExpandableDataItem
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.ExpandableFaqRvLayoutBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder

internal class DefaultBannerBgViewHolder(
    private val binding: ExpandableFaqRvLayoutBinding,
    private val onItemClick: (Int) -> Unit
) : BaseViewHolder(binding.root) {
    fun bind(faq: ExpandableDataItem.DefaultBannerWithBGIsExpandedDataType) {
        binding.expandableFaqLayout.isExpanded = faq.isExpanded
        binding.tvQuestion.text = faq.question
        binding.tvAnswer.text = faq.answer

        binding.root.setDebounceClickListener {
            onItemClick.invoke(absoluteAdapterPosition)
            binding.containerArrowIv.animate()
                .rotation(if (binding.expandableFaqLayout.isExpanded) 0f else 180f).start()
            binding.expandableFaqLayout.toggle()
        }
    }
}