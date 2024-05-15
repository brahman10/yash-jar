package com.jar.app.core_ui.faq

import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.core_base.domain.model.Faq
import com.jar.app.core_ui.databinding.ItemMilestoneFaqBinding
import com.jar.app.core_ui.extension.setDebounceClickListener

class FaqViewHolder(private val binding: ItemMilestoneFaqBinding) : BaseViewHolder(binding.root) {

    init {
        binding.root.setDebounceClickListener {
            binding.ivExpand.animate()
                .rotation(if (binding.expandableLayout.isExpanded) 0f else 180f).start()
            binding.expandableLayout.toggle()
        }
    }

    fun setFaq(faq: Faq) {
        binding.ivExpand.rotation = if (binding.expandableLayout.isExpanded) 180f else 0f
        binding.tvQuestion.text = faq.question
        binding.tvAnswer.text = faq.answer
    }
}