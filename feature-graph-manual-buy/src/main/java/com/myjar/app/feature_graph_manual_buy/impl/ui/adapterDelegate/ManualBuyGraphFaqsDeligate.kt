package com.myjar.app.feature_graph_manual_buy.impl.ui.adapterDelegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.calendarView.viewholder.PostSetupPageItem
import com.myjar.app.feature_graph_manual_buy.databinding.FaqsGraphManualBuyLayoutBinding
import com.myjar.app.feature_graph_manual_buy.impl.model.ManualBuyGraphFaqsItem
import com.myjar.app.feature_graph_manual_buy.impl.ui.viewHolder.FaqsManualBuyGraphViewHolder

class ManualBuyGraphFaqsDeligate (private val onClick:()->Unit): AdapterDelegate<List<PostSetupPageItem>>() {
    override fun isForViewType(items: List<PostSetupPageItem>, position: Int): Boolean {
        return items[position] is ManualBuyGraphFaqsItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = FaqsGraphManualBuyLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FaqsManualBuyGraphViewHolder(
            binding
        ){
            onClick.invoke()
        }
    }

    override fun onBindViewHolder(
        items: List<PostSetupPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (holder is FaqsManualBuyGraphViewHolder && item is ManualBuyGraphFaqsItem)
            holder.bind(item)
    }
}