package com.jar.app.feature_round_off.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class InitiateDetectedRoundOffsPaymentRequest(
    @SerialName("txnAmt")
    val txnAmt: Float,

    @SerialName("orderId")
    val orderId: String,

    @SerialName("percent")
    val percent: Int? = null,

    @SerialName("isPartial")
    val isPartial: Boolean? = null,

    @SerialName("skip")
    val skip: Boolean? = null
) : Parcelable