package com.jar.app.feature_gold_delivery.impl.helper

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.bold
import androidx.core.text.toSpannable
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.customFont
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIData
import com.jar.app.feature_transactions_common.shared.CommonTransactionStatus
import com.jar.app.core_base.data.dto.GoldDeliveryTrackingStatusEnum
import com.jar.app.feature_gold_delivery.impl.ui.GoldDeliveryTransactionStatus
import dev.icerock.moko.resources.ColorResource
import java.lang.ref.WeakReference

internal object Utils {

    fun buildSpannableStringForGoldPrice(price: Float, context: WeakReference<Context>): Spannable {
        val context = context.get() ?: return "".toSpannable()
        val prefix = context.getString(R.string.feature_gold_delivery_current_gold_price_rs)
        val suffix = context.getString(
            R.string.feature_gold_delivery_n_double_gm,
            price
        )

        val spannable = SpannableStringBuilder()
            .append(prefix)
            .append(" ")

        try {
            ResourcesCompat.getFont(context, com.jar.app.core_ui.R.font.inter_bold)?.let {
                spannable.customFont({ append(suffix) }, it)
            } ?: run {
                spannable.bold { append(suffix) }
            }
        } catch (ex: Exception) {
            spannable.bold { append(suffix) }
        }
        return spannable.toSpannable()
    }
    fun calculateQuantityItemsString(context: WeakReference<Context>, cartItemData: CartAPIData?): String {
        val quantity = cartItemData?.cartItemData?.sumOf { it?.quantity ?: 0 }.orZero()
        return "$quantity " + (context.get()?.resources?.getQuantityString(R.plurals.items, quantity) ?: "Items")
    }
    fun getColorForGoldDeliveryTransactionStatus(context: Context, statusEnum: String?): Int {
        val transactionStatus = statusEnum?.uppercase()
        val status: GoldDeliveryTransactionStatus =
            GoldDeliveryTransactionStatus.values().find { it.name == transactionStatus }
                ?: GoldDeliveryTransactionStatus.DEFAULT
        return ContextCompat.getColor(context, status.color)
    }

    fun getGoldDeliveryTrackingStatusEnum(status: String?): GoldDeliveryTrackingStatusEnum {
        val transactionStatus = status?.uppercase()
        return GoldDeliveryTrackingStatusEnum.values().find { it.name == transactionStatus }
            ?: GoldDeliveryTrackingStatusEnum.PENDING
    }

    fun getStatusEnum(status: String?): GoldDeliveryTrackingStatusEnum {
        val transactionStatus = status?.uppercase()
        return GoldDeliveryTrackingStatusEnum.values().find { it.name == transactionStatus }
            ?: GoldDeliveryTrackingStatusEnum.PENDING
    }

    fun getColorForCommonTransactionStatus(statusEnum: String? = null): ColorResource {
        val transactionStatus = statusEnum?.uppercase()
        val status: CommonTransactionStatus =
            CommonTransactionStatus.values().find { it.name == transactionStatus }
                ?: CommonTransactionStatus.DEFAULT
        return status.getColor()
    }
}