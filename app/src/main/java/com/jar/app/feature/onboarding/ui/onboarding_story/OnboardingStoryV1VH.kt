package com.jar.app.feature.onboarding.ui.onboarding_story

import android.graphics.drawable.Drawable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.data.event.OnboardingStoryImageResourceReadyEvent
import com.jar.app.base.util.setHtmlText
import com.jar.app.databinding.CellOnboardingStoryVariant1Binding
import org.greenrobot.eventbus.EventBus

class OnboardingStoryV1VH(
    private val binding: CellOnboardingStoryVariant1Binding,
    private val onResourceReadyVH: (Long?, Int?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var onImageLoadStart: Long? = null

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

    fun bindData(data: com.jar.app.feature_onboarding.shared.domain.model.Stories) {
        onImageLoadStart = System.currentTimeMillis()
        data.title?.let { binding.tvHeading.setHtmlText(it) }
        data.description?.let {
            binding.tvDescription.isVisible = true
            binding.tvDescription.setHtmlText(it)
        }
        Glide.with(itemView.context)
            .load(data.bgImage)
            .listener(requestListener)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    onResourceReadyVH.invoke((System.currentTimeMillis() - onImageLoadStart!!),position)
                    binding.roundedImageView.setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }
}