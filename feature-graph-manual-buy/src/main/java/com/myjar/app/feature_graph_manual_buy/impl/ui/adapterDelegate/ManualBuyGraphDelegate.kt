package com.myjar.app.feature_graph_manual_buy.impl.ui.adapterDelegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.calendarView.viewholder.PostSetupPageItem
import com.myjar.app.feature_graph_manual_buy.databinding.ManualBuyGraphLayoutBinding
import com.myjar.app.feature_graph_manual_buy.impl.model.ManualBuyGraphItem
import com.myjar.app.feature_graph_manual_buy.impl.ui.viewHolder.ManualBuyGraphViewHolder

internal class ManualBuyGraphDelegate: AdapterDelegate<List<PostSetupPageItem>>() {
    override fun isForViewType(items: List<PostSetupPageItem>, position: Int): Boolean {
        return items[position] is ManualBuyGraphItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ManualBuyGraphLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ManualBuyGraphViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<PostSetupPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (holder is ManualBuyGraphViewHolder && item is ManualBuyGraphItem)
            holder.bind(item)
    }
}