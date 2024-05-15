package com.jar.app.feature_daily_investment.impl.ui.faq

import androidx.core.text.HtmlCompat
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.ExpandableFaqRvLayoutBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_daily_investment.shared.domain.model.GenericFAQs

internal class DailySavingsV2ExpandableFaqViewHolder(
    private val binding: ExpandableFaqRvLayoutBinding,
    private val onItemClick: (Int) -> Unit
) : BaseViewHolder(binding.root) {

    fun bind(faq: GenericFAQs) {
        binding.expandableFaqLayout.isExpanded = faq.isExpanded
        binding.tvQuestion.text = faq.question
        val answer =  faq.answer.replace("\n", "<br>")
        binding.tvAnswer.text = HtmlCompat.fromHtml(
            answer,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )

        binding.clMain.setDebounceClickListener {
            onItemClick.invoke(bindingAdapterPosition)
            if (binding.expandableFaqLayout.isExpanded) {
                binding.expandableFaqLayout.isExpanded = false
                val animation = android.view.animation.AnimationUtils.loadAnimation(
                    context,
                    R.anim.item_rotate_180
                )
                binding.containerArrowIv.startAnimation(animation)
                binding.containerArrowIv.postOnAnimation {
                    binding.containerArrowIv.setImageResource(R.drawable.core_ui_ic_arrow_up)
                }
            } else {
                binding.expandableFaqLayout.isExpanded = true
                val animation = android.view.animation.AnimationUtils.loadAnimation(
                    context,
                    R.anim.item_rotate_180
                )
                binding.containerArrowIv.startAnimation(animation)
                binding.containerArrowIv.postOnAnimation {
                    binding.containerArrowIv.setImageResource(R.drawable.core_ui_ic_arrow_down)
                }
            }
        }
    }
}