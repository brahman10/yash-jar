package com.jar.app.feature_gold_delivery.impl.ui.store_item.wishlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar.app.base.util.dp
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.ItemCartWishlistBinding
import com.jar.app.feature_gold_delivery.databinding.ItemGoldDeliveryFaqHeaderBinding
import com.jar.app.feature_gold_delivery.shared.data.WishlistData
import com.jar.app.feature_gold_delivery.shared.domain.model.WishlistAPIData

class WishlistAdapter(
    private val onEditClick: (cart: WishlistAPIData) -> Unit,
    private val onDeleteClick: (cart: WishlistAPIData) -> Unit,
    private val onCheckedChange: () -> Unit,
) : PagingDataAdapter<WishlistData, ViewHolder>(DIFF_CALLBACK) {

    companion object {
        const val WISHLIST_HOLDER_HEADER = 1
        const val WISHLIST_HOLDER_DETAIL = 2
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WishlistData>() {
            override fun areItemsTheSame(
                oldItem: WishlistData,
                newItem: WishlistData
            ): Boolean {
                return ((oldItem is WishlistData.WishlistHeader && newItem is WishlistData.WishlistHeader && oldItem.title == newItem.title) ||
                        (oldItem is WishlistData.WishlistBody && newItem is WishlistData.WishlistBody && oldItem.body.id == newItem.body.id))
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: WishlistData,
                newItem: WishlistData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is WishlistData.WishlistHeader -> WISHLIST_HOLDER_HEADER
            is WishlistData.WishlistBody -> WISHLIST_HOLDER_DETAIL
            else -> throw java.lang.RuntimeException("Item View holder isn't correct")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            WISHLIST_HOLDER_HEADER -> {
                val binding = ItemGoldDeliveryFaqHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return NewCartMyOrderHeaderVH(binding)
            }

            WISHLIST_HOLDER_DETAIL -> {
                val binding =
                    ItemCartWishlistBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return NewCartMyOrderVH(binding, onEditClick, onDeleteClick, onCheckedChange)
            }

            else -> {
                throw java.lang.RuntimeException("Item View holder isn't correct")
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemViewType = getItemViewType(position)
        when (itemViewType) {
            WISHLIST_HOLDER_HEADER -> {
                getItem(position)?.let {
                    (holder as NewCartMyOrderHeaderVH).bindData(it as WishlistData.WishlistHeader)
                }

            }

            WISHLIST_HOLDER_DETAIL -> {
                getItem(position)?.let {
                    (holder as NewCartMyOrderVH).bindData(it as WishlistData.WishlistBody)
                }

            }
        }
    }

    inner class NewCartMyOrderHeaderVH(
        private val binding: ItemGoldDeliveryFaqHeaderBinding
    ) : BaseViewHolder(binding.root) {
        fun bindData(it: WishlistData.WishlistHeader) {
            binding.tvHeader.text = it.title
        }
    }


    inner class NewCartMyOrderVH(
        private val binding: ItemCartWishlistBinding,
        private val onEditClick: (cart: WishlistAPIData) -> Unit,
        private val onDeleteClick: (cart: WishlistAPIData) -> Unit,
        private val onCheckedChange: () -> Unit,
    ) : BaseViewHolder(binding.root) {

        fun bindData(data: WishlistData.WishlistBody) {
            setChecked(binding.checkbox, data.isChecked)

            binding.root.setDebounceClickListener {
                if (data.body.inStock == true) {
                    data.isChecked = !data.isChecked
                    setChecked(binding.checkbox, data.isChecked)
                    onCheckedChange()
                }
            }
            if (data.body.inStock == true) {
                binding.checkbox.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        com.jar.app.core_ui.R.color.color_776E94
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                binding.checkbox.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        com.jar.app.core_ui.R.color.color_3c3357
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            binding.cartView.editIv.setDebounceClickListener {
                onEditClick(data.body)
            }
            binding.cartView.cartQuantity.isVisible = false
            data.body.icon?.takeIf { it.isNotEmpty() }?.let {
                Glide.with(binding.cartView.cartImageView).load(it).transform(RoundedCorners(10.dp))
                    .into(binding.cartView.cartImageView)
            } ?: run {
                Glide.with(binding.cartView.cartImageView).load(R.drawable.placeholder_item)
                    .transform(
                        RoundedCorners(10.dp)
                    ).into(binding.cartView.cartImageView)
            }
            binding.cartView.deleteIv.setDebounceClickListener {
                onDeleteClick(data.body)
            }
            binding.cartView.tvCartName.text = data.body.label

            data.body.discountOnTotal?.let {
                binding.cartView.tvDiscountPrice.text = binding.root.context.getString(
                    R.string.feature_buy_gold_currency_sign_x_string,
                    it.getFormattedAmount()
                )
                binding.cartView.tvDiscountPrice.isVisible = true
            } ?: run {
                binding.cartView.tvDiscountPrice.isVisible = false
            }
            binding.cartView.tvPrice.text = binding.root.context.getString(
                R.string.feature_buy_gold_currency_sign_x_string,
                data.body.amount?.getFormattedAmount()
            )
            binding.cartView.tvQuantity.text = data.body.volume.orZero().toString() + " gm"
            binding.cartView.outOfStockGroup.isVisible = !((data.body.inStock) ?: true)
        }

        private fun setChecked(checkbox: AppCompatImageView, checked: Boolean) {
            checkbox.setImageResource(
                if (checked) R.drawable.checkbox_checked else R.drawable.checkbox_unchecked
            )
            if (checked) {
                checkbox.colorFilter = null
            }
        }
    }
}
