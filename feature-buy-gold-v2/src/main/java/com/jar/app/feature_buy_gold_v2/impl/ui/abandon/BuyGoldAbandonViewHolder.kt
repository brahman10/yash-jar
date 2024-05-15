package com.jar.app.feature_buy_gold_v2.impl.ui.abandon

import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_buy_gold_v2.databinding.FeatureBuyGoldAbandonStepCellBinding
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldAbandonSteps


internal class BuyGoldAbandonViewHolder(
    private val binding: FeatureBuyGoldAbandonStepCellBinding,
) : BaseViewHolder(binding.root) {

    fun bind(data: BuyGoldAbandonSteps){

        Glide.with(binding.root)
            .load(data.imageUrl)
            .priority(Priority.HIGH)
            .into(binding.ivIcon)

        binding.tvTitle.setHtmlText(data.title)

    }

}