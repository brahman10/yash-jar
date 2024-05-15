package com.jar.app.feature_savings_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class ManualPaymentInfo(
    @SerialName("txnAmt")
    val txnAmt: Float,

    @SerialName("title")
    val title: String? = null,

    @SerialName("orderId")
    val orderId: String,

    @SerialName("description")
    val description: String? = null,

    @SerialName("nudgeText")
    val nudgeText: String? = null,
) : Parcelable