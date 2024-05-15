package com.jar.app.feature_buy_gold_v2.shared.domain.model

import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class InitiateBuyGoldRequest(

    @SerialName("amount")
    val amount: Float,

    @SerialName("volume")
    val volume: Float,

    @SerialName("priceResponse")
    val fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null,

    @SerialName("requestType")
    val requestType: String,

    @SerialName("paymentProvider")
    val paymentProvider: String,

    @SerialName("auspiciousTimeId")
    val auspiciousTimeId: String? = null,

    @SerialName("couponCodeId")
    val couponCodeId: String? = null,

    @SerialName("couponCode")
    val couponCode: String? = null,

    @SerialName("offerAmount")
    val offerAmount: Double? = null,

    @SerialName("giftingId")
    val giftingId: String? = null,

    @SerialName("deliveryOrderId")
    val deliveryOrderId: String? = null,

    @SerialName("deliveryMakingCharge")
    val deliveryMakingCharge: Double? = null,

    @SerialName("jarWinningsUsedAmount")
    val jarWinningsUsedAmount: Float = 0.0f,
    //added for Dhanteras flow
    @SerialName("userProductId")
    val userProductId: String? = null,

    //added for Gold Lease Flow
    @SerialName("leaseId")
    val leaseId: String? = null,

    @SerialName("flowContext")
    val flowContext: String? = null,
)