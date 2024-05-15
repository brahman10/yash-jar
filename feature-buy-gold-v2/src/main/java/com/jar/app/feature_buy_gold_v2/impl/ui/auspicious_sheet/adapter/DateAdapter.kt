package com.jar.app.feature_buy_gold_v2.impl.ui.auspicious_sheet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.feature_buy_gold_v2.databinding.CellDateBinding
import com.jar.app.feature_buy_gold_v2.impl.ui.auspicious_sheet.view_holder.DateViewHolder
import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousTime

internal class DateAdapter : RecyclerView.Adapter<DateViewHolder>() {

    var dateList = ArrayList<AuspiciousTime>()

    fun submitList(list: List<AuspiciousTime>) {
        dateList = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DateViewHolder(
        CellDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        if (dateList.isNullOrEmpty().not()) {
            holder.setDate(dateList[position])
        }
    }

    override fun getItemCount(): Int {
        return dateList.size
    }
}