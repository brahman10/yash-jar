package com.jar.app.feature_buy_gold_v2.shared.domain.model

import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentType
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldUpiApp
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class InitiateBuyGoldData(
    val buyGoldPaymentType: BuyGoldPaymentType,
    val selectedUpiApp: BuyGoldUpiApp?
): Parcelable