package com.jar.app.feature_gold_delivery.impl.ui.cart_items

import androidx.core.view.isVisible
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.ItemCartHolderBinding
import com.jar.app.feature_gold_delivery.shared.domain.model.CartItemData

class CartItemViewHolder(
    private val binding: ItemCartHolderBinding,
    private val onDeleteClick: (address: CartItemData) -> Unit,
) :
    BaseViewHolder(binding.root) {

    fun setCartItemData(address: CartItemData) {
        binding.deleteIv.setDebounceClickListener {
            onDeleteClick(address)
        }
        binding.tvCartName.text = address.label
        binding.tvPrice.text = binding.root.context.getString(
            R.string.feature_buy_gold_currency_sign_x_string,
            address.totalAmount?.getFormattedAmount()
        )
        address.discountOnTotal?.let {
            binding.tvDiscountPrice.text = binding.root.context.getString(
                R.string.feature_buy_gold_currency_sign_x_string,
                address.amount?.getFormattedAmount()
            )
            binding.tvDiscountPrice.isVisible = true
        } ?: run {
            binding.tvDiscountPrice.isVisible = false
        }
        binding.tvQuantity.text = context.getString(R.string.item_quantity_gm, address.quantity, address.volume)
    }
}