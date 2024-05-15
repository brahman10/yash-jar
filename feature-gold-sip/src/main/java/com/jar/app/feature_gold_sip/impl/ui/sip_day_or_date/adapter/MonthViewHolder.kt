package com.jar.app.feature_gold_sip.impl.ui.sip_day_or_date.adapter

import androidx.core.view.updatePadding
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipCellMonthDayBinding
import com.jar.app.feature_gold_sip.shared.domain.model.WeekOrMonthData

internal class MonthViewHolder(
    private val binding: FeatureGoldSipCellMonthDayBinding,
    private val onItemClick: (WeekOrMonthData, Int) -> Unit
) :
    BaseViewHolder(binding.root) {

    private var weekOrMonthData: WeekOrMonthData? = null

    init {
        binding.tvMonthDate.setDebounceClickListener {
            weekOrMonthData?.let {
                onItemClick.invoke(it, bindingAdapterPosition)
            }
        }
    }

    fun setMonthData(weekOrMonthData: WeekOrMonthData) {
        this.weekOrMonthData = weekOrMonthData
        binding.tvMonthDate.updatePadding(
            if (weekOrMonthData.value > 9) 14.dp else 16.dp,
            14.dp,
            if (weekOrMonthData.value > 9) 14.dp else 16.dp,
            14.dp
        )

        binding.tvMonthDate.text = weekOrMonthData.value.toString()
        binding.tvMonthDate.isSelected = weekOrMonthData.isSelected
    }
}