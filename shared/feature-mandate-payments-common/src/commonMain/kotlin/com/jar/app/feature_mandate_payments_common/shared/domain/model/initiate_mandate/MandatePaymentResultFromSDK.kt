package com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate

import com.jar.app.feature_mandate_payments_common.shared.domain.model.paytm_intent.PaytmIntentAutoPayPaymentResultData
import com.jar.app.feature_mandate_payments_common.shared.domain.model.paytm_sdk.PaytmAutoPayPaymentResultData
import com.jar.app.feature_mandate_payments_common.shared.domain.model.phonepe.PhonePeAutoPayResultData
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class MandatePaymentResultFromSDK(

    @SerialName("phonePe")
    val phonePeAutoPayResultData: PhonePeAutoPayResultData? = null,

    @SerialName("paytm")
    val paytmSdkAutoPayPaymentResultData: PaytmAutoPayPaymentResultData? = null,

    @SerialName("paytmIntent")
    val paytmIntentAutoPayPaymentResultData: PaytmIntentAutoPayPaymentResultData? = null,
) : Parcelable