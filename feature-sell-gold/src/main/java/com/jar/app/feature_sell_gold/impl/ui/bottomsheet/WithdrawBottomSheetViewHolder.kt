package com.jar.app.feature_sell_gold.impl.ui.bottomsheet

import com.bumptech.glide.Glide
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_sell_gold.databinding.FeatureSellGoldBottomsheetRvViewBinding
import com.jar.app.feature_sell_gold.shared.domain.models.Steps

class WithdrawBottomSheetViewHolder(
    private val binding: FeatureSellGoldBottomsheetRvViewBinding,
    ) : BaseViewHolder(binding.root) {

        fun bind(steps: Steps){
            Glide.with(context)
                .load(steps.iconUrl)
                .into(binding.ivRecyclerView)

            binding.tvHeading.text = steps.title
        }
}