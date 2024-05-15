package com.jar.app.feature_homepage.impl.ui.viba

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellVibaPrimeryItemBinding
import com.jar.app.feature_homepage.shared.domain.model.viba.VibaHorizontalCardData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class FirstCardViewHolder(
    private val binding: FeatureHomepageCellVibaPrimeryItemBinding,
    private val uiScope: CoroutineScope,
    private val onItemClick: (VibaHorizontalCardData) -> Unit
) : BaseViewHolder(binding.root) {

    var job: Job? = null

    fun binding(vibaHorizontalCardData: VibaHorizontalCardData) {
        binding.tvHeader.text = vibaHorizontalCardData.title
        Glide.with(binding.root)
            .load(vibaHorizontalCardData.icon)
            .into(binding.ivHeaderIcon)

        val descriptionTitleList = mutableListOf<String>()
        val descriptionFooterList = mutableListOf<String>()
        vibaHorizontalCardData.description?.map {
            descriptionTitleList.add(it.title)
            descriptionFooterList.add(it.description)
        }

        if (vibaHorizontalCardData.background.overlayImage != null) {
            binding.ivThumbnail.isVisible = true
            Glide.with(binding.root)
                .load(vibaHorizontalCardData.background.overlayImage)
                .transform(RoundedCorners(16))
                .into(binding.ivThumbnail)
        } else {
            val background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    Color.parseColor(vibaHorizontalCardData.background.startColor),
                    Color.parseColor(vibaHorizontalCardData.background.endColor),
                )
            )
            background.cornerRadius = 16f
            binding.ivThumbnail.isVisible = false
            binding.clHolder.background = background
        }
        var currentIndex = 0
        job?.cancel()
        job = uiScope.launch {
            while (isActive) {
                val animation = AnimationUtils.loadAnimation(binding.root.context, R.anim.fade_in)
                binding.tvDescription.startAnimation(animation)
                binding.tvFooter.startAnimation(animation)
                binding.tvDescription.text = descriptionTitleList[currentIndex]
                binding.tvFooter.text = descriptionFooterList[currentIndex]
                currentIndex = (currentIndex + 1) % descriptionTitleList.size
                delay(3000L)
            }
        }

        binding.clHolder.setDebounceClickListener {
            onItemClick.invoke(vibaHorizontalCardData)
        }
    }
}