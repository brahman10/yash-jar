package com.jar.android.feature_post_setup.impl.ui.setup_details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_base.domain.model.GenericFaqItem
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.ExpandableFaqRvLayoutBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder

internal class PostSetupFaqAdapter(
    private val onItemClick: (Int, GenericFaqItem) -> Unit
) : ListAdapter<GenericFaqItem, PostSetupFaqAdapter.PostSetupFaqViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GenericFaqItem>() {
            override fun areItemsTheSame(
                oldItem: GenericFaqItem, newItem: GenericFaqItem
            ): Boolean {
                return oldItem.question == newItem.question
            }

            override fun areContentsTheSame(
                oldItem: GenericFaqItem, newItem: GenericFaqItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PostSetupFaqViewHolder(
        ExpandableFaqRvLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: PostSetupFaqViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class PostSetupFaqViewHolder(
        private val binding: ExpandableFaqRvLayoutBinding
    ) : BaseViewHolder(binding.root) {

        fun bind(faq: GenericFaqItem) {
            binding.root.background =
                ContextCompat.getDrawable(binding.root.context, com.jar.app.core_ui.R.drawable.rounded_black_bg_8dp)
            binding.root.backgroundTintList = ContextCompat.getColorStateList(
                binding.root.context,
                com.jar.app.core_ui.R.color.color_2e2942
            )
            binding.root.setPadding(10,20,10,20)
            binding.expandableFaqLayout.isExpanded = faq.isExpanded
            binding.tvQuestion.text = faq.question
            val answer = faq.answer.replace("\n", "<br>")
            binding.tvAnswer.text = HtmlCompat.fromHtml(
                answer,
                HtmlCompat.FROM_HTML_MODE_COMPACT
            )
            binding.expandableFaqLayout.backgroundTintList = ContextCompat.getColorStateList(
                binding.root.context,
                com.jar.app.core_ui.R.color.color_2e2942
            )
            binding.clMain.setDebounceClickListener {
                onItemClick.invoke(bindingAdapterPosition, faq)
                if (binding.expandableFaqLayout.isExpanded) {
                    binding.expandableFaqLayout.isExpanded = false
                    val animation = android.view.animation.AnimationUtils.loadAnimation(
                        context,
                        com.jar.app.core_ui.R.anim.item_rotate_180
                    )
                    binding.containerArrowIv.startAnimation(animation)
                    binding.containerArrowIv.postOnAnimation {
                        binding.containerArrowIv.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_up)
                        binding.containerArrowIv.backgroundTintList = ContextCompat.getColorStateList(
                            binding.root.context,
                            com.jar.app.core_ui.R.color.white
                        )
                    }
                } else {
                    binding.expandableFaqLayout.isExpanded = true
                    val animation = android.view.animation.AnimationUtils.loadAnimation(
                        context,
                        com.jar.app.core_ui.R.anim.item_rotate_180
                    )
                    binding.containerArrowIv.startAnimation(animation)
                    binding.containerArrowIv.postOnAnimation {
                        binding.containerArrowIv.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_down)
                        binding.containerArrowIv.backgroundTintList = ContextCompat.getColorStateList(
                            binding.root.context,
                            com.jar.app.core_ui.R.color.white
                        )
                    }
                }
            }
        }
    }
}