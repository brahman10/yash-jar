package com.myjar.app.feature_graph_manual_buy.impl.ui.adapterDelegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.jar.app.core_ui.calendarView.viewholder.PostSetupPageItem
import com.myjar.app.feature_graph_manual_buy.databinding.BottomImageLayoutBinding
import com.myjar.app.feature_graph_manual_buy.impl.model.BottomImageItem
import com.myjar.app.feature_graph_manual_buy.impl.ui.viewHolder.BottomImageViewHolder

class BottomImageDeligate: AdapterDelegate<List<PostSetupPageItem>>() {
    override fun isForViewType(items: List<PostSetupPageItem>, position: Int): Boolean {
        return items[position] is BottomImageItem

    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = BottomImageLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BottomImageViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<PostSetupPageItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val item = items[position]
        if (holder is BottomImageViewHolder && item is BottomImageItem)
            holder.bind(item)
    }

}