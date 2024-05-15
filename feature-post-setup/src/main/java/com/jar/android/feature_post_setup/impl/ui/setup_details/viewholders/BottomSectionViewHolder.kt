package com.jar.android.feature_post_setup.impl.ui.setup_details.viewholders

import com.bumptech.glide.Glide
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupCellBottomSectionBinding
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_post_setup.domain.model.BottomSectionPageItem

internal class BottomSectionViewHolder(private val binding: FeaturePostSetupCellBottomSectionBinding) :
    BaseViewHolder(binding.root) {

    fun setView(bottomSectionPageItem: BottomSectionPageItem) {
        Glide.with(binding.root.context)
            .load(bottomSectionPageItem.imageUrl)
            .into(binding.ivAsset)
    }
}