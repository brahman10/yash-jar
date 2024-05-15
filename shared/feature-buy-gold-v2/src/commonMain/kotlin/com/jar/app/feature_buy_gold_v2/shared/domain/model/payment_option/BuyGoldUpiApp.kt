package com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class BuyGoldUpiApp(
    val payerApp: String,
    var isSelected: Boolean = false,
    val headerType: BuyGoldPaymentSectionHeaderType
): Parcelable

enum class BuyGoldPaymentSectionHeaderType {
    RECOMMENDED,
    UPI_APPS
}