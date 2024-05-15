package com.jar.app.feature_buy_gold_v2.shared.domain.model

import com.jar.app.feature_one_time_payments.shared.domain.model.UpiApp
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@kotlinx.serialization.Serializable
data class BuyGoldV2BreakdownData(
    val couponCode: String?,
    val totalPayableAmount: Float,
    val goldValue: Float,
    val goldVolume: Float,
    val applicableTax: Float,
    val goldPurchasePrice: Float?,
    val newPaymentStripForBreakdown: NewPaymentStripForBreakdown?
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class NewPaymentStripForBreakdown(
    val paymentAppChooserText: String,
    val ctaText: String,
    val lastUsedUpiApp: UpiApp,
    val maxPaymentMethodsCount: Int
) : Parcelable