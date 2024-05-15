package com.jar.app.feature_sell_gold_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class SellGoldResponse(

    @SerialName("amount")
    val amount: Float,

    @SerialName("goldVolume")
    val goldVolume: Float,

    @SerialName("remainingVol")
    val remainingVol: Float,

    @SerialName("orderId")
    val orderId: String? = null,

    @SerialName("invoiceLink")
    val invoiceLink: String? = null,

    @SerialName("txnStatus")
    val txnStatus: String
) : Parcelable