package com.jar.app.feature_mandate_payments_common.shared.domain.model.paytm_intent

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PaytmIntentAutopayPaymentResponse(
    @SerialName("orderId")
    val orderId: String,

    @SerialName("redirectUrl")
    val redirectUrl: String
): Parcelable