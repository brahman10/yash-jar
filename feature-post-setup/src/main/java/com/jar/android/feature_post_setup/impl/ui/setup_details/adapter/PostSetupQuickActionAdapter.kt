package com.jar.android.feature_post_setup.impl.ui.setup_details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellQuickActionBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_post_setup.domain.model.setting.PostSetupQuickActionItem

class PostSetupQuickActionAdapter(private val onItemClick: (PostSetupQuickActionItem) -> Unit) :
    ListAdapter<PostSetupQuickActionItem, PostSetupQuickActionAdapter.PostSetupQuickActionViewHolder>(
        DIFF_CALLBACK
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PostSetupQuickActionItem>() {
            override fun areItemsTheSame(
                oldItem: PostSetupQuickActionItem, newItem: PostSetupQuickActionItem
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: PostSetupQuickActionItem, newItem: PostSetupQuickActionItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PostSetupQuickActionViewHolder(
            FeaturePostSetupCellQuickActionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: PostSetupQuickActionViewHolder, position: Int) {
        getItem(position)?.let { holder.setQuickAction(it) }
    }

    inner class PostSetupQuickActionViewHolder(private val binding: FeaturePostSetupCellQuickActionBinding) :
        BaseViewHolder(binding.root) {

        fun setQuickAction(postSetupQuickActionItem: PostSetupQuickActionItem) {
            binding.tvTitle.text = postSetupQuickActionItem.title
            Glide.with(binding.root.context).load(postSetupQuickActionItem.icon)
                .into(binding.ivAction)

            binding.root.setDebounceClickListener {
                onItemClick.invoke(postSetupQuickActionItem)
            }
        }
    }
}