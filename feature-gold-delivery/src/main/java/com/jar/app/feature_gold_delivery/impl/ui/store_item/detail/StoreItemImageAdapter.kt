package com.jar.app.feature_gold_delivery.impl.ui.store_item.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_delivery.databinding.CellStoreItemImageBinding

class StoreItemImageAdapter : ListAdapter<String, StoreItemImageViewHolder>(ITEM_CALLBACK) {

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemImageViewHolder {
        val binding =
            CellStoreItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoreItemImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreItemImageViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setImage(it)
        }
    }
}