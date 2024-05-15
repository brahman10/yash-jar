package com.jar.app.feature_gold_delivery.impl.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.label_and_value.LabelAndValue
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.ItemSucessScreenCartitemBinding
import com.jar.app.feature_one_time_payments_common.shared.OrderDetails
import java.lang.ref.WeakReference

object CartSuccessHelper {

    fun inflateItemView(
        orderDetails: OrderDetails,
        parentGroup: WeakReference<ViewGroup>
    ): ItemSucessScreenCartitemBinding {
        val parent = parentGroup.get()
        val binding = ItemSucessScreenCartitemBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )
        parent?.context?.let {
            Glide.with(it).load(orderDetails.icon)
                .into(binding.coinIv)
        }
        binding.coinName.text = orderDetails.label
        binding.coinQuantity.text = parent?.context?.getString(
            R.string.item_quantity_gm,
            orderDetails.quantity?.toIntOrNull(),
            orderDetails.volume
        )
        orderDetails.discountOnTotal?.let {
            binding.tvDiscount.isVisible = true
            binding.tvDiscount.text = parent?.context?.getString(
                R.string.rupee_x_in_double_strike,
                (orderDetails.discountOnTotal.orZero())
            )
        } ?: run {
            binding.tvDiscount.isVisible = false
        }
        binding.tvPrice.text = parent?.context?.getString(
            R.string.feature_buy_gold_currency_sign_x_string,
            orderDetails.amount?.getFormattedAmount()
        )
        return binding
    }

    fun curateLabelAndValueForDetailsList(
        orderId: String,
        placedOn: String? = null,
        paidVia: String? = null,
        upiID: String? = null,
        context: WeakReference<Context>
    ): MutableList<LabelAndValue> {
        val list = mutableListOf<LabelAndValue>()
        list.add(
            LabelAndValue(
                context.get()?.getString(R.string.order_id) ?: "",
                orderId,
                showCopyToClipBoardIcon = true,
                labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        placedOn?.let {
            list.add(
                LabelAndValue(
                    context.get()?.getString(R.string.placed_on) ?: "", it,
                    labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )
        }
        paidVia?.let {
            list.add(
                LabelAndValue(
                    context.get()?.getString(R.string.paid_via) ?: "", it,
                    labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )
        }
        upiID?.let {
            list.add(
                LabelAndValue(
                    context.get()?.getString(R.string.upi) ?: "", it,
                    labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )
        }
        return list
    }
}