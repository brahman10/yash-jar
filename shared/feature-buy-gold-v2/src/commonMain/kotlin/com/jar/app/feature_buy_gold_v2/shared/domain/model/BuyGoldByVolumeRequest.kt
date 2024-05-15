package com.jar.app.feature_buy_gold_v2.shared.domain.model

import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BuyGoldByVolumeRequest(
    @SerialName("volume")
    val volume: Float,

    @SerialName("fetchCurrentGoldPriceResponse")
    val fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse,

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

    @SerialName("paymentGateway")
    val paymentGateway: com.jar.app.core_base.domain.model.OneTimePaymentGateway,

    @SerialName("deliveryMakingCharge")
    val deliveryMakingCharge: Double? = null,

    @SerialName("jarWinningsUsedAmount")
    val jarWinningsUsedAmount: Float = 0.0f,

    @SerialName("weeklyChallengeFlow")
    val weeklyChallengeFlow: Boolean = false,

    //added for Gold Lease Flow
    @SerialName("leaseId")
    val leaseId: String? = null
)