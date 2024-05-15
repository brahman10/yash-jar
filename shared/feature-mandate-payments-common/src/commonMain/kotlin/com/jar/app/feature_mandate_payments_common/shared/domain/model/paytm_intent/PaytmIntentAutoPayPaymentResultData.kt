package com.jar.app.feature_mandate_payments_common.shared.domain.model.paytm_intent

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class PaytmIntentAutoPayPaymentResultData(
    @SerialName("orderId")
    val orderId: String
) : Parcelable