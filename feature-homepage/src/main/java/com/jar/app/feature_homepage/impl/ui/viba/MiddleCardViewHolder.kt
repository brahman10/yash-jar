package com.jar.app.feature_homepage.impl.ui.viba

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellVibaSecondaryItemBinding
import com.jar.app.feature_homepage.shared.domain.model.viba.VibaHorizontalCardData

internal class MiddleCardViewHolder(
    private val binding: FeatureHomepageCellVibaSecondaryItemBinding,
    private val onItemClick: (VibaHorizontalCardData) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun binding(vibaHorizontalCardData: VibaHorizontalCardData) {
        binding.tvDescription.text = vibaHorizontalCardData.description?.getOrNull(0)?.title
        binding.tvFooter.text = vibaHorizontalCardData.description?.getOrNull(0)?.description
        Glide.with(binding.root)
            .load(vibaHorizontalCardData.icon)
            .into(binding.ivIcon)
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
            binding.ivThumbnail.background = background
        }
        binding.clHolder.setDebounceClickListener {
            onItemClick.invoke(vibaHorizontalCardData)
        }
    }
}