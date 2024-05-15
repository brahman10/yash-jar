package com.jar.app.core_ui.onboarding_stories

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.data.event.OnboardingStoryImageResourceReadyEvent
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.databinding.CellOnboardingStoryBinding
import com.jar.app.core_ui.onboarding_stories.data.Stories
import org.greenrobot.eventbus.EventBus

class OnboardingStoryAdapter :
    ListAdapter<Stories, OnboardingStoryAdapter.OnboardingStoryVH>(DIFF_UTIL) {

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
        CellOnboardingStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: OnboardingStoryVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    inner class OnboardingStoryVH(
        private val binding: CellOnboardingStoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val requestListener = object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                val isFromCache =
                    dataSource === DataSource.MEMORY_CACHE || dataSource === DataSource.DATA_DISK_CACHE
                val currentTime = System.currentTimeMillis()
                EventBus.getDefault().post(OnboardingStoryImageResourceReadyEvent(timeTaken = currentTime, isFromCache = isFromCache))
                return false
            }
        }

        fun bindData(data: Stories) {
            Glide.with(itemView.context)
                .load(data.image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .listener(requestListener)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        binding.tvTitle.isInvisible = false
                        if(data.title.isNullOrEmpty().not()){
                            data.title?.let { binding.tvTitle.setHtmlText(it) }
                        }
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