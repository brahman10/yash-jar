package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.abandonScreen

import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_daily_investment.shared.domain.model.Steps
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentAbandonScreenRvViewBinding

internal class DailySavingsV2AbandonViewHolder(
    private val binding: FeatureDailyInvestmentAbandonScreenRvViewBinding,
) :
    BaseViewHolder(binding.root) {

        fun bind(data: Steps){

            Glide.with(binding.root)
                .load(data.imageUrl)
                .priority(Priority.HIGH)
                .into(binding.imIcon)

            binding.tvTitle.setHtmlText(data.title)

        }

}