package com.jar.app.feature_gold_sip.impl.ui.sip_day_or_date.adapter

import androidx.core.content.ContextCompat
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_sip.R
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipCellWeekDayBinding
import com.jar.app.feature_gold_sip.shared.domain.model.WeekOrMonthData

internal class WeekViewHolder(
    private val binding: FeatureGoldSipCellWeekDayBinding,
    private val onItemClick: (WeekOrMonthData, Int) -> Unit
) :
    BaseViewHolder(binding.root) {
    private var weekOrMonthData: WeekOrMonthData? = null

    init {
        binding.root.setDebounceClickListener {
            weekOrMonthData?.let { onItemClick.invoke(it, bindingAdapterPosition) }
        }
    }

    fun setWeekData(weekOrMonthData: WeekOrMonthData) {
        this.weekOrMonthData = weekOrMonthData
        binding.tvWeekDay.text = weekOrMonthData.text?.let { getCustomString(it.stringRes) }
        binding.root.isSelected = weekOrMonthData.isSelected
        binding.circleView.background = ContextCompat.getDrawable(
            binding.root.context,
            if (weekOrMonthData.isSelected) com.jar.app.core_ui.R.drawable.ic_tick_green
            else R.drawable.feature_gold_sip_bg_outline_circle_small
        )
    }
}