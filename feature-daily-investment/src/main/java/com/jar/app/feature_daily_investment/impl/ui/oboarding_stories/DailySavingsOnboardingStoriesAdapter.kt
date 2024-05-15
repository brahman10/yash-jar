package com.jar.app.feature_daily_investment.impl.ui.oboarding_stories

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.databinding.CellDailySavingOnboardingStoryBinding
import com.jar.app.feature_daily_investment.shared.domain.model.Stories

class DailySavingsOnboardingStoriesAdapter :
    ListAdapter<Stories, DailySavingsOnboardingStoriesAdapter.OnboardingStoryVH>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Stories>() {
            override fun areItemsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem.image == newItem.image
            }

            override fun areContentsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OnboardingStoryVH(
        CellDailySavingOnboardingStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: OnboardingStoryVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class OnboardingStoryVH(
        private val binding: CellDailySavingOnboardingStoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(data: Stories) {
            Glide.with(itemView.context)
                .load(data.image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        binding.tvTitle.isInvisible = false
                        data.title?.let { binding.tvTitle.setHtmlText(it) }
                        binding.tvDescription.isInvisible = false
                        binding.tvDescription.text = data.description
                        binding.shimmerPlaceholder.stopShimmer()
                        binding.shimmerPlaceholder.isVisible = false
                        binding.ivImage.isInvisible = false
                        binding.ivImage.setImageDrawable(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }
    }
}