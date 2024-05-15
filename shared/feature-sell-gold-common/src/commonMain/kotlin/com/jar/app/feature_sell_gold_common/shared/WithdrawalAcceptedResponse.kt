package com.jar.app.feature_sell_gold_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class WithdrawalAcceptedResponse(
    @SerialName("amount")
    val amount: Float? = null,
    @SerialName("orderId")
    val orderId: String? = null,
    @SerialName("responseStatus")
    val responseStatus: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("statusMessage")
    val statusMessage: String? = null,
    @SerialName("volume")
    val volume: String? = null,
    @SerialName("header")
    val header: String? = null,
    @SerialName("info")
    val info: String? = null,
    @SerialName("sellGoldResponse")
    val sellGoldResponse: SellGoldResponse
) : Parcelable