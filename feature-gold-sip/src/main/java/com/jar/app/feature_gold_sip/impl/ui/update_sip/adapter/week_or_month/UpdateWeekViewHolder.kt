package com.jar.app.feature_gold_sip.impl.ui.update_sip.adapter.week_or_month

import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipCellUpdateWeekDayBinding
import com.jar.app.feature_gold_sip.shared.domain.model.WeekOrMonthData

internal class UpdateWeekViewHolder(
    private val binding: FeatureGoldSipCellUpdateWeekDayBinding,
    private val onItemClick: (WeekOrMonthData, Int) -> Unit
) :
    BaseViewHolder(binding.root) {
    private var weekOrMonthData: WeekOrMonthData? = null

    init {
        binding.tvWeekDay.setDebounceClickListener {
            weekOrMonthData?.let {
                onItemClick.invoke(it, bindingAdapterPosition)
            }
        }
    }

    fun setWeekData(weekOrMonthData: WeekOrMonthData) {
        this.weekOrMonthData = weekOrMonthData
        binding.tvWeekDay.text = weekOrMonthData.text?.let { getCustomString(it.stringRes) }
        binding.tvWeekDay.isSelected = weekOrMonthData.isSelected
    }
}