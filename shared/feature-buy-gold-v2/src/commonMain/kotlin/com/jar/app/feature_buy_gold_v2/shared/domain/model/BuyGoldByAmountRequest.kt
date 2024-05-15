package com.jar.app.feature_buy_gold_v2.shared.domain.model

import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class BuyGoldByAmountRequest(
    @SerialName("amount")
    val amount: Float,

    @SerialName("fetchCurrentGoldPriceResponse")
    val fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse? = null,

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
    val paymentGateway: OneTimePaymentGateway,

    @SerialName("deliveryMakingCharge")
    val deliveryMakingCharge: Double? = null,

    @SerialName("jarWinningsUsedAmount")
    val jarWinningsUsedAmount: Float = 0.0f,

    @SerialName("userProductId")
    val userProductId: String? = null,

    @SerialName("weeklyChallengeFlow")
    val weeklyChallengeFlow: Boolean = false,

    @SerialName("flowContext")
    val flowContext:String? = null
) : Parcelable