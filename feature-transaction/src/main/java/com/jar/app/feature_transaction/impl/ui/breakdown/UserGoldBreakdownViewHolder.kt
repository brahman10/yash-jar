package com.jar.app.feature_transaction.impl.ui.breakdown

import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_transaction.databinding.FeatureTransactionCellGoldBreakdownBinding
import com.jar.app.feature_transaction.shared.domain.model.UserGoldBreakdown

class UserGoldBreakdownViewHolder(private val binding: FeatureTransactionCellGoldBreakdownBinding) : BaseViewHolder(binding.root) {

    fun setUserGoldBreakdown(userGoldBreakdown: com.jar.app.feature_transaction.shared.domain.model.UserGoldBreakdown) {
        binding.tvKey.text = userGoldBreakdown.key
        binding.tvValue.text = userGoldBreakdown.value
    }
}