package com.jar.app.feature.user_gold_breakdown.ui

import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.databinding.CellGoldBreakdownBinding
import com.jar.app.feature_homepage.shared.domain.model.user_gold_breakdown.UserGoldBreakdown

class UserGoldBreakdownViewHolder(private val binding: CellGoldBreakdownBinding) : BaseViewHolder(binding.root) {

    fun setUserGoldBreakdown(userGoldBreakdown: UserGoldBreakdown) {
        binding.tvKey.text = userGoldBreakdown.key
        binding.tvValue.text = userGoldBreakdown.value
    }
}