package com.jar.app.feature_buy_gold_v2.impl.ui.auspicious_sheet.view_holder

import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_buy_gold_v2.databinding.CellAuspiciousDayBinding
import com.jar.app.feature_buy_gold_v2.impl.ui.auspicious_sheet.adapter.DateAdapter
import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousDate

class AuspiciousDateViewHolder(private val binding: CellAuspiciousDayBinding) : BaseViewHolder(binding.root) {

    private val dateAdapter = DateAdapter()

    fun setAuspiciousDate(auspiciousDate: AuspiciousDate) {
        binding.tvDate.text = auspiciousDate.date
        binding.tvEventName.text = auspiciousDate.name
        binding.rvDates.layoutManager = LinearLayoutManager(context)
        binding.rvDates.adapter = dateAdapter
        dateAdapter.submitList(auspiciousDate.auspiciousTimes)
    }
}