package com.jar.app.core_ui.widget.ticker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.core_ui.databinding.CellTickerTextBinding
import com.jar.app.core_ui.widget.ticker.model.TickerData

class TickerAdapter : ListAdapter<TickerData, TickerViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TickerData>() {
            override fun areItemsTheSame(oldItem: TickerData, newItem: TickerData): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: TickerData, newItem: TickerData): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TickerViewHolder {
        val binding =
            CellTickerTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TickerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TickerViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setTickerData(it)
        }
    }
}