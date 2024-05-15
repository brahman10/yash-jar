package com.jar.app.feature_gold_delivery.impl.ui.store_item.list

import android.graphics.Paint
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.CellGoldStoreItemBinding
import com.jar.app.feature_gold_delivery.impl.ui.store_item.detail.renderAlertStrip
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductV2

class StoreItemViewHolder(
    private val binding: CellGoldStoreItemBinding,
    private val onItemClick: (storeItem: ProductV2) -> Unit,
    private val onLikeClicked: ((storeItem: ProductV2) -> Unit)?
) :
    BaseViewHolder(binding.root) {

    init {
        binding.root.setDebounceClickListener {
            storeItem?.let(onItemClick)
        }
        onLikeClicked?.let { click ->
            binding.likeIV.setDebounceClickListener {
                storeItem?.let { it1 -> click.invoke(it1) }
            }
        }
    }

    private var storeItem: ProductV2? = null

    fun setStoreItem(storeItem: ProductV2) {
        this.storeItem = storeItem
        binding.tvName.text = storeItem.label
        val availableVolumeV2 = storeItem.availableVolumes?.getOrNull(0)
        val price = availableVolumeV2?.goldDeliveryPrice

        binding.tvPrice.text =
            context.getString(
                R.string.starting_from_rupee_x_in_double,
                price?.total
            )
        when {
            price?.discountOnTotal.orZero() == price?.total -> {
                binding.tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvPriceDiscount.text = context.getString(R.string.free_limited_time_offer)
                binding.tvPriceDiscount.isVisible = true
            }

            price?.discountOnTotal.orZero() > 0 -> {
                binding.tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvPriceDiscount.text =
                    context.getString(
                        R.string.rupee_x_in_double_strike,
                        (price?.total.orZero() - price?.discountOnTotal.orZero())
                    )
                binding.tvPriceDiscount.isVisible = true
            }

            else -> {
                binding.tvPriceDiscount.isVisible = false
            }
        }
        if (storeItem.wishListed == true) {
            binding.likeIV.setImageResource(R.drawable.ic_heart_red)
            binding.likeIV.isVisible = true
        } else {
            binding.likeIV.isVisible = false
        }
        storeItem.alertStrip?.let {
            renderAlertStrip(
                it,
                binding.productLabelBg,
                binding.productLabelIv,
                binding.productLabelTv,
                binding.productLabelBgShadow,
            )
            binding.alertStrip.isVisible = true
        } ?: run {
            binding.alertStrip.isVisible = false
        }
        val all = storeItem.availableVolumes?.all { it?.inStock == false }
        binding.outOfStockGroup.isVisible = all ?: false
        val noOfItems = storeItem.calculateInStockItems()
        if (noOfItems > 0) {
            binding.tvWeights.text = context.resources.getQuantityString(R.plurals.items_available, noOfItems, noOfItems)
            binding.tvWeights.isVisible = true
        } else {
            binding.tvWeights.visibility = View.INVISIBLE
        }
        Glide.with(itemView)
            .load(availableVolumeV2?.media?.images?.getOrNull(0))
            .into(binding.ivImage)
    }
}