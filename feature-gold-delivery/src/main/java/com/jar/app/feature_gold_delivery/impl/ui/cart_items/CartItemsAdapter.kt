package com.jar.app.feature_gold_delivery.impl.ui.cart_items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.jar.app.feature_gold_delivery.databinding.ItemCartHolderBinding
import com.jar.app.feature_gold_delivery.shared.domain.model.CartItemData

class CartItemsAdapter(
    private val onDeleteClick: (address: CartItemData) -> Unit,
) : ListAdapter<CartItemData, CartItemViewHolder>(ITEM_CALLBACK) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding =
            ItemCartHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setCartItemData(it)
        }
    }
}