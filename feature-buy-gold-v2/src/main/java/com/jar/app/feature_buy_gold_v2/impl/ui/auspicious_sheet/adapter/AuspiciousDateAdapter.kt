package com.jar.app.feature_buy_gold_v2.impl.ui.auspicious_sheet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.jar.app.feature_buy_gold_v2.databinding.CellAuspiciousDayBinding
import com.jar.app.feature_buy_gold_v2.impl.ui.auspicious_sheet.view_holder.AuspiciousDateViewHolder
import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousDate

class AuspiciousDateAdapter : PagingDataAdapter<AuspiciousDate, AuspiciousDateViewHolder>(
    DIFF_UTIL
) {

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<AuspiciousDate>() {
            override fun areItemsTheSame(oldItem: AuspiciousDate, newItem: AuspiciousDate): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: AuspiciousDate, newItem: AuspiciousDate): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AuspiciousDateViewHolder(
        CellAuspiciousDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: AuspiciousDateViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setAuspiciousDate(it)
        }
    }
}