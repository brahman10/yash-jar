package com.jar.app.feature_gold_lease.impl.ui.new_lease_page

import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_lease.databinding.CellGoldLeaseComparisonBinding
import com.jar.app.feature_gold_lease.shared.domain.model.LeaseComparisonTableRowsList

internal class GoldLeaseComparisonViewHolder(
    private val binding: CellGoldLeaseComparisonBinding
) : BaseViewHolder(binding.root) {
    fun bind(data: LeaseComparisonTableRowsList) {
        binding.tvComparisonTitle.setHtmlText(data.rowTitle.orEmpty())
        binding.tvComparisonGoldValue.setHtmlText(data.commonGoldValue.orEmpty())
        binding.tvComparisonGoldXValue.setHtmlText(data.leaseGoldValue.orEmpty())
    }
}
