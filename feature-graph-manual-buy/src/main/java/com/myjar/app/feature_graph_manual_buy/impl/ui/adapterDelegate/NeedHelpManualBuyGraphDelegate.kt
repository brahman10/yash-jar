package com.myjar.app.feature_graph_manual_buy.impl.ui.adapterDelegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.calendarView.viewholder.PostSetupPageItem
import com.myjar.app.feature_graph_manual_buy.databinding.NeedHelpManualBuyLayoutBinding
import com.myjar.app.feature_graph_manual_buy.impl.model.NeedHelpManualBuyGraphItem
import com.myjar.app.feature_graph_manual_buy.impl.ui.viewHolder.NeedHelpManualBuyGraphViewHolder

class NeedHelpManualBuyGraphDelegate(private val onClick:(String)->Unit) : AdapterDelegate<List<PostSetupPageItem>>() {
    override fun isForViewType(items: List<PostSetupPageItem>, position: Int): Boolean {
        return items[position] is NeedHelpManualBuyGraphItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = NeedHelpManualBuyLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return NeedHelpManualBuyGraphViewHolder(binding) {
            onClick.invoke(it)
        }
    }

    override fun onBindViewHolder(
        items: List<PostSetupPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (item is NeedHelpManualBuyGraphItem && holder is NeedHelpManualBuyGraphViewHolder)
            holder.bind(item)
    }
}