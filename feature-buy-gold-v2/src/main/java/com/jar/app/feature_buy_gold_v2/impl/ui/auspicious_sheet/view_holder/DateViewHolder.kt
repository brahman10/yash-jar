package com.jar.app.feature_buy_gold_v2.impl.ui.auspicious_sheet.view_holder

import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.CellDateBinding
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousTime

internal class DateViewHolder(private val binding: CellDateBinding) : BaseViewHolder(binding.root) {

    fun setDate(auspiciousTime: AuspiciousTime) {
        binding.tvDate.text = getCustomStringFormatted(context, MR.strings.feature_buy_gold_v2_start_time_s_end_time_s, auspiciousTime.startTime, auspiciousTime.endTime)
    }
}