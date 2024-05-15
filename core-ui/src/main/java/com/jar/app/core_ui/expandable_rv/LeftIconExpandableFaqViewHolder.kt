package com.jar.app.core_ui.expandable_rv

import com.bumptech.glide.Glide
import com.jar.app.core_base.domain.model.ExpandableDataItem
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.LeftIconWithSeperatorExpandableLayoutBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder

internal class LeftIconExpandableFaqViewHolder(
    private val binding: LeftIconWithSeperatorExpandableLayoutBinding,
    private val onItemClick: (Int) -> Unit
) : BaseViewHolder(binding.root) {

    fun bind(faq: ExpandableDataItem.LeftIconIsExpandedDataType) {
        binding.expandableContentContainer.isExpanded = faq.isExpanded
        binding.headerTv.text = faq.question
        binding.tvAnswer.text = faq.answer
        faq.resId?.let { binding.leftImage.setImageResource(it) }
        faq.imageUrl?.let {
            Glide.with(binding.root).load(it).into(binding.leftImage)
        }

        binding.root.setDebounceClickListener {
            onItemClick?.invoke(absoluteAdapterPosition)
            binding.containerArrowIv.animate()
                .rotation(if (binding.expandableContentContainer.isExpanded) 0f else 180f).start()
            binding.expandableContentContainer.toggle()
        }
    }
}