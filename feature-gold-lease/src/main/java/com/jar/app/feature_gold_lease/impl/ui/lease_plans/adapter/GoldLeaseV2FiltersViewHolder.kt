package com.jar.app.feature_gold_lease.impl.ui.lease_plans.adapter

import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.CellGoldLeaseFilterBinding
import com.jar.app.feature_gold_lease.shared.domain.model.LeasePlanFilterInfoList

internal class GoldLeaseV2FiltersViewHolder(
    private val binding: CellGoldLeaseFilterBinding,
    private val onFilterClicked: (leasePlanFilterInfoList: LeasePlanFilterInfoList) -> Unit
): BaseViewHolder(binding.root) {
    private var leasePlanFilterInfoList: LeasePlanFilterInfoList? = null

    init {
        binding.root.setDebounceClickListener {
            leasePlanFilterInfoList?.let {
                if (it.isSelected.not()) {
                    onFilterClicked.invoke(it)
                }
            }
        }
    }

    fun bind(data: LeasePlanFilterInfoList) {
        this.leasePlanFilterInfoList = data

        binding.llRoot.setBackgroundResource(
            if (data.isSelected) R.drawable.feature_gold_lease_bg_rounded_7745ff_6dp else R.drawable.feature_gold_lease_bg_rounded_272239_6dp_stroke_3c3357
        )
        val filterText =
            if (data.isSelected) {
                if (data.leasePlanCount.orZero() <= 0)
                    data.leasePlanListingFilterName.orEmpty()
                else
                    "${data.leasePlanListingFilterName.orEmpty()} (${data.leasePlanCount.orZero()})"
            } else {
                data.leasePlanListingFilterName.orEmpty()
            }

        binding.tvFilter.setTextColor(
            ContextCompat.getColor(
                context, if (data.isSelected) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_ACA1D3
            )
        )
        binding.tvFilter.setTypeface(binding.tvFilter.typeface, if (data.isSelected) Typeface.BOLD else Typeface.NORMAL)
        binding.tvFilter.text = filterText
    }
}