package com.jar.app.feature_buy_gold_v2.impl.domain.model

import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentSectionHeaderType

fun BuyGoldPaymentSectionHeaderType.getHeaderTextResourceId(): Int {
    return when(this) {
        BuyGoldPaymentSectionHeaderType.RECOMMENDED -> {
            com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_recommended
        }
        BuyGoldPaymentSectionHeaderType.UPI_APPS -> {
            com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_using_upi_apps
        }
    }
}