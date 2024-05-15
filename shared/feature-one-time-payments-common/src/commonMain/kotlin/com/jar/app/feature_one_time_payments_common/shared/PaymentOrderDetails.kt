package com.jar.app.feature_one_time_payments_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PaymentOrderDetails(
    @SerialName("paidVia")
    val paidVia: String? = null,
    @SerialName("paymentOrderId")
    val paymentOrderId: String? = null,
    @SerialName("placedOn")
    val placedOn: String? = null,
    @SerialName("transactionId")
    val transactionId: String? = null,
    @SerialName("upiId")
    val upiId: String? = null
) : Parcelable