package com.jar.app.feature_gold_delivery.impl.ui.store_item.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_delivery.databinding.CellStoreCartBinding
import com.jar.app.feature_gold_delivery.shared.domain.model.CartItemData

class StoreCartAdapter(
    private val onDeleteClick: (address: CartItemData) -> Unit,
    private val onMinusClick: (address: CartItemData, quantity: Int) -> Unit,
    private val onAddClick: (address: CartItemData) -> Unit,
    private val onEditClick: (address: CartItemData) -> Unit,
    private val isCheckoutMode: () -> Boolean
) : ListAdapter<CartItemData, StoreCartViewHolder>(ITEM_CALLBACK) {

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<CartItemData>() {
            override fun areItemsTheSame(oldItem: CartItemData, newItem: CartItemData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CartItemData, newItem: CartItemData): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreCartViewHolder {
        val binding =
            CellStoreCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoreCartViewHolder(binding, onDeleteClick, onMinusClick, onAddClick, onEditClick, isCheckoutMode)
    }

    override fun onBindViewHolder(holder: StoreCartViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setCartItemData(it)
        }
    }
}