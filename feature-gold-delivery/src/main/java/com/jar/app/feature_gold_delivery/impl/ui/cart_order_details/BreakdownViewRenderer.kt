package com.jar.app.feature_gold_delivery.impl.ui.cart_order_details

import android.content.Context
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.impl.ui.store_item.cart.BreakdownDoubleLineData
import com.jar.app.feature_gold_delivery.impl.ui.store_item.cart.BreakdownGradientDividerData
import com.jar.app.feature_gold_delivery.impl.ui.store_item.cart.BreakdownSingleLineData
import com.jar.app.feature_gold_delivery.impl.ui.store_item.cart.BreakdownViewData
import com.jar.app.feature_gold_delivery.impl.ui.store_item.cart.BreakdownViewRenderer
import com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails
import java.lang.ref.WeakReference

internal object BreakdownViewRenderer {

    fun getBreakdownListFromCart(
        context: WeakReference<Context>,
        it: NewTransactionDetails
    ): MutableList<BreakdownViewData> {
        val list = mutableListOf<BreakdownViewData>()
        it.paymentDetails?.let {
            list.add(
                BreakdownSingleLineData(
                    it.label + " (${it.volume}gm | ${it.quantity} item)",
                    BreakdownViewRenderer.getFormattedAmount(
                        context,
                        it.amount.orZero().getFormattedAmount()
                    )
                )
            )
        }
        list.add(BreakdownGradientDividerData())
        it.paymentDetails?.let {
            list.add(
                BreakdownDoubleLineData(
                    context.get()?.getString(R.string.delivery_and_making_charges) ?: "",
                    BreakdownViewRenderer.getFormattedAmount(
                        context,
                        it.deliveryMakingCharge.orZero().getFormattedAmount()
                    ),
                    it.label ?: ""
                )
            )
        }
        it.paymentDetails?.let { paymentDetails ->
            list.add(
                BreakdownDoubleLineData(
                    "GST",
                    BreakdownViewRenderer.getFormattedAmount(
                        context,
                        paymentDetails.gst.orZero().getFormattedAmount()
                    ),
                    paymentDetails.label.orEmpty()
                )
            )
        }
        list.add(BreakdownGradientDividerData())
        it.paymentDetails?.totalAmount?.let {
            list.add(
                BreakdownSingleLineData(
                    context.get()?.getString(R.string.total_value) ?: "",
                    BreakdownViewRenderer.getFormattedAmount(context, it.getFormattedAmount())
                )
            )
        }
        return list
    }
}