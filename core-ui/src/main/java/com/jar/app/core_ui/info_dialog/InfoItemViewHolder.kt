package com.jar.app.core_ui.info_dialog

import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.core_ui.databinding.CoreUiCellInfoItemBinding
import com.jar.app.core_base.domain.model.InfoItem
import com.jar.app.core_ui.view_holder.BaseViewHolder

class InfoItemViewHolder(private val binding: CoreUiCellInfoItemBinding) :
    BaseViewHolder(binding.root) {

    fun setInfoItem(infoItem: InfoItem) {
        Glide.with(itemView).load(infoItem.icon).into(binding.ivInfoIcon)
        binding.tvTitle.text = infoItem.title
        binding.tvDescription.text = infoItem.description
        binding.tvTitle.isVisible = infoItem.title.isNullOrBlank().not()
        binding.tvDescription.isVisible = infoItem.description.isNullOrBlank().not()
    }
}