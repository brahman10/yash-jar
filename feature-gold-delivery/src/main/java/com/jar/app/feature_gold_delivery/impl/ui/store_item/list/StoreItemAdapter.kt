package com.jar.app.feature_gold_delivery.impl.ui.store_item.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_delivery.databinding.CellGoldStoreItemBinding
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductV2

class StoreItemAdapter(
    private val onItemClick: (storeItem: ProductV2) -> Unit,
    private val onLikeClicked: ((storeItem: ProductV2) -> Unit)? = null,
) : ListAdapter<ProductV2, StoreItemViewHolder>(ITEM_CALLBACK) {

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<ProductV2>() {
            override fun areItemsTheSame(oldItem: ProductV2, newItem: ProductV2): Boolean {
                return oldItem == newItem // todo compare with id
            }

            override fun areContentsTheSame(oldItem: ProductV2, newItem: ProductV2): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemViewHolder {
        val binding =
            CellGoldStoreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoreItemViewHolder(binding, onItemClick, onLikeClicked)
    }

    override fun onBindViewHolder(holder: StoreItemViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setStoreItem(it)
        }
    }
}