package com.jar.app.feature_gold_delivery.impl.ui.store_item.cart

import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar.app.base.util.dp
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.CellStoreCartBinding
import com.jar.app.feature_gold_delivery.impl.ui.store_item.detail.CartItemQuantityViewListener
import com.jar.app.feature_gold_delivery.shared.domain.model.CartItemData

class StoreCartViewHolder(
    private val binding: CellStoreCartBinding,
    private val onDeleteClick: (address: CartItemData) -> Unit,
    private val onMinusClick: (address: CartItemData, quantity: Int) -> Unit,
    private val onAddClick: (address: CartItemData) -> Unit,
    private val onEditClick: (address: CartItemData) -> Unit,
    private val isCheckoutMode: () -> Boolean
) :
    BaseViewHolder(binding.root) {

    fun setCartItemData(address: CartItemData) {
        val isCheckoutMode = isCheckoutMode()
        binding.deleteIv.setDebounceClickListener {
            onDeleteClick(address)
        }
        binding.cartQuantity.setListener(object : CartItemQuantityViewListener {
            override fun counterAdded() {
                onAddClick(address)
            }

            override fun counterSubtracted(quantity: Int) {
                onMinusClick(address, quantity)
            }
        })
        binding.editIv.setDebounceClickListener {
            onEditClick(address)
        }
        address.quantity?.let {
            binding.cartQuantity.setCount(it)
        }
        address.icon?.takeIf { it.isNotEmpty() }?.let {
            Glide.with(itemView).load(it).transform(RoundedCorners(10.dp))
                .into(binding.cartImageView)
        } ?: run {
            binding.cartImageView.setImageResource(R.drawable.ic_no_notification_placeholder)
        }
        binding.tvCartName.text = address.label

        address.discountOnTotal?.let {
            binding.tvDiscountPrice.text = binding.root.context.getString(
                R.string.feature_buy_gold_currency_sign_x_string,
                address.amount?.getFormattedAmount()
            )
            binding.tvDiscountPrice.isVisible = true
        } ?: run {
            binding.tvDiscountPrice.isVisible = false
        }

        binding.tvPrice.text = binding.root.context.getString(
            R.string.feature_buy_gold_currency_sign_x_string,
            (address.totalAmount
                ?: (address.amount.orZero() + address.deliveryMakingCharge.orZero()).getFormattedAmount())
        )

        binding.deleteIv.isVisible = !isCheckoutMode
        binding.cartQuantity.isVisible = !isCheckoutMode
        binding.editIv.isVisible = !isCheckoutMode
        binding.tvQuantity.text = if (!isCheckoutMode) address.volume.orZero().toString() + " gm" else
            "${address.volume.orZero().toString()} gm | ${address.quantity?.orZero()} Item"
    }
}