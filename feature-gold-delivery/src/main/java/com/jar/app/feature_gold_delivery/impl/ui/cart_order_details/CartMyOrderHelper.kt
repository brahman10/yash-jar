package com.jar.app.feature_gold_delivery.impl.ui.cart_order_details

import android.content.Context
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_base.data.dto.GoldDeliveryTrackingStatusEnum
import com.jar.app.core_ui.label_and_value.LabelAndValue
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.impl.helper.Utils.getGoldDeliveryTrackingStatusEnum
import com.jar.app.feature_gold_delivery.impl.ui.store_item.cart.BreakdownImageLineData
import com.jar.app.feature_gold_delivery.impl.ui.store_item.cart.BreakdownNormalDividerData
import com.jar.app.feature_gold_delivery.impl.ui.store_item.cart.BreakdownViewData
import com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails
import java.lang.ref.WeakReference

internal object  CartMyOrderHelper {
    fun isCancelOrderVisible(it: GoldDeliveryTrackingStatusEnum): Boolean {
        return (it in arrayOf(
            GoldDeliveryTrackingStatusEnum.PENDING,
            GoldDeliveryTrackingStatusEnum.DISPATCHED,
            GoldDeliveryTrackingStatusEnum.PACKAGE_PENDING,
            GoldDeliveryTrackingStatusEnum.PACKAGE_PENDING,
            GoldDeliveryTrackingStatusEnum.PACKED,
        ))
    }

    fun constructLabelValueListForTracking(
        it: NewTransactionDetails,
        context: WeakReference<Context>
    ): List<LabelAndValue> {
        val list = mutableListOf<LabelAndValue>()
        val status = getGoldDeliveryTrackingStatusEnum(it.trackingInfo?.status)
        it.trackingInfo?.trackingId?.let {
            list.add(
                LabelAndValue(
                    context.get()?.getString(R.string.tracking_id) ?: "Tracking ID",
                    it,
                    labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                ),
            )
        }
        if (status != null && it.trackingInfo?.statusText != null) {
            val label = LabelAndValue(
                context.get()?.getString(R.string.status) ?: "Status",
                it.trackingInfo?.statusText.orEmpty(),
                valueColorString = it.trackingInfo?.trackingStatusColor.orEmpty(),
                labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
            list.add(label)
        }

        it.trackingInfo?.estimatedDeliveryDate?.let {
            list.add(LabelAndValue(context.get()?.getString(R.string.estimated_delivery) ?: "Estimated Delivery", it,
                labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle))
        }

        return list
    }

    fun constructLabelValueForPaymentSource(
        it: NewTransactionDetails,
        context: WeakReference<Context>
    ): MutableList<BreakdownViewData> {
        val list = mutableListOf<BreakdownViewData>()
        it.paymentSource?.userAmount?.let {
            list.add(
                BreakdownImageLineData(
                    R.drawable.jar_payment,
                    context.get()?.getString(R.string.used_jar_balance) ?: "",
                    context.get()?.getString(
                        R.string.feature_buy_gold_currency_sign_x_string,
                        it.getFormattedAmount()
                    ) ?: ""
                )
            )
        }
        it.paymentSource?.paymentAmount?.let { amount ->
            if (list.isNotEmpty())
                list.add(BreakdownNormalDividerData())
            list.add(
                BreakdownImageLineData(
                    if (it.paymentSource?.imageUrl.isNullOrBlank()) R.drawable.jar_payment else null,
                    it.paymentSource?.payerVpa ?: it?.paymentSource?.lastFourDigits
                    ?: it?.paymentSource?.paymentMethod ?: "",
                    context.get()?.getString(
                        R.string.feature_buy_gold_currency_sign_x_string,
                        amount.getFormattedAmount()
                    ) ?: "",
                    imageUrl = it.paymentSource?.imageUrl
                )
            )
        }
        return list
    }
}