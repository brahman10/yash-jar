package com.jar.app.core_ui.widget.ticker.ui

import com.jar.app.core_ui.databinding.CellTickerTextBinding
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.core_ui.widget.ticker.model.TickerData

class TickerViewHolder(private val binding: CellTickerTextBinding) : BaseViewHolder(binding.root) {

    fun setTickerData(tickerData: TickerData) {
        binding.tvTickerText.text = tickerData.title
        binding.tvTickerText.setCompoundDrawablesWithIntrinsicBounds(tickerData.icon, 0, 0, 0)
    }
}