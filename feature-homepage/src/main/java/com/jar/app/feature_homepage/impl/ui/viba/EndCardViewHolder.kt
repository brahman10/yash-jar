package com.jar.app.feature_homepage.impl.ui.viba

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_homepage.databinding.FeatureHomepageCellVibaEndItemBinding
import com.jar.app.feature_homepage.shared.domain.model.viba.VibaHorizontalCardData

internal class EndCardViewHolder(
    private val binding: FeatureHomepageCellVibaEndItemBinding,
    private val onItemClick: (VibaHorizontalCardData) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun binding(vibaHorizontalCardData: VibaHorizontalCardData) {
        Glide.with(binding.root)
            .load(vibaHorizontalCardData.icon)
            .into(binding.ivHeaderIcon)
        binding.tvHeader.text = vibaHorizontalCardData.title
        if (vibaHorizontalCardData.background.overlayImage != null) {
            binding.ivThumbnail.isVisible = true
            Glide.with(binding.root)
                .load(vibaHorizontalCardData.background.overlayImage)
                .transform(RoundedCorners(16))
                .into(binding.ivThumbnail)
        } else {
            binding.ivThumbnail.isVisible = false
            val background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    Color.parseColor(
                        vibaHorizontalCardData.background.startColor.toString()
                    ),
                    Color.parseColor(
                        vibaHorizontalCardData.background.endColor.toString()
                    ),
                )
            )
            background.cornerRadius = 16f
            binding.clHolder.background = background
        }

        binding.clHolder.setDebounceClickListener {
            onItemClick.invoke(
                vibaHorizontalCardData
            )
        }
    }
}
