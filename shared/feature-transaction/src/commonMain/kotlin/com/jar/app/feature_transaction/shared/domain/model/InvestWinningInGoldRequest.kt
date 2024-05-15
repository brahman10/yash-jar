package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class InvestWinningInGoldRequest(
    @SerialName("amount")
    val amount: Double,

    @SerialName("priceResponse")
    val priceResponse: FetchCurrentGoldPriceResponse
)