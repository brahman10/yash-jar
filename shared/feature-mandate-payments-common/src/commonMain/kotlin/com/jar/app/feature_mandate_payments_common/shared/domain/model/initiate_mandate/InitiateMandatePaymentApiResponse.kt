package com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate

import com.jar.app.feature_mandate_payments_common.shared.domain.model.paytm_intent.PaytmIntentAutopayPaymentResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.paytm_sdk.PaytmSdkAutoPayPaymentResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.phonepe.PhonePeAutoPayResponse
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class InitiateMandatePaymentApiResponse(

    @SerialName("packageName")
    var packageName: String? = null,

    @SerialName("paytm")
    val paytm: PaytmSdkAutoPayPaymentResponse? = null,

    @SerialName("paytmIntent")
    val paytmIntent: PaytmIntentAutopayPaymentResponse? = null,

    @SerialName("phonePe")
    val phonePe: PhonePeAutoPayResponse? = null
) : Parcelable