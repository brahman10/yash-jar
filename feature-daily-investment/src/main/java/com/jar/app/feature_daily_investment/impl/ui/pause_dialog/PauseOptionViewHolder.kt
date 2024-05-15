package com.jar.app.feature_daily_investment.impl.ui.pause_dialog

import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.shared.domain.model.PauseDailySavingData
import com.jar.app.feature_daily_investment.databinding.FeatureDailyCellPauseAutoInvestTimeBinding

class PauseOptionViewHolder constructor(
    private val binding: FeatureDailyCellPauseAutoInvestTimeBinding,
    private val onClick: (pauseOption: PauseDailySavingData) -> Unit
) : BaseViewHolder(binding.root) {

    private var pauseDailySavingData: PauseDailySavingData? = null

    init {
        binding.root.setOnClickListener {
            pauseDailySavingData?.let(onClick)
        }
    }

    fun setPauseOption(pauseDailySavingData: PauseDailySavingData) {
        this.pauseDailySavingData = pauseDailySavingData
        binding.tvNumDay.text = pauseDailySavingData.pauseDailySavingsOption.timeValue.toString()
        binding.tvDayHeader.text = context.resources.getQuantityString(
            R.plurals.days,
            pauseDailySavingData.pauseDailySavingsOption.timeValue
        )

        if (pauseDailySavingData.isSelected)
            binding.root.setBackgroundResource(R.drawable.feature_daily_savings_bg_pause_option_selected)
        else
            binding.root.setBackgroundResource(R.drawable.feature_daily_savings_bg_pause_option_unselected)
    }
}