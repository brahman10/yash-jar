package com.jar.app.feature_one_time_payments.shared.domain.model.juspay

import com.jar.app.core_base.util.toJsonElement
import com.jar.app.feature_one_time_payments.shared.BuildKonfig
import com.jar.app.feature_one_time_payments.shared.util.OneTimePaymentConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

@kotlinx.serialization.Serializable
internal data class InitiateUpiCollect(
    @SerialName("requestId")
    val requestId: String,

    @SerialName("orderId")
    val orderId: String,

    @SerialName("custVpa")
    val custVpa: String,

    @SerialName("endUrl")
    val endUrl: String,

    @SerialName("clientAuthToken")
    val clientAuthToken: String
) {

    fun toJsonObject(): JsonObject {
        val endUrls = JsonArray(
            listOf(
                endUrl.toRegex().toString().toJsonElement()
            )
        )

        val innerPayload = JsonObject(
            mapOf(
                "action" to OneTimePaymentConstants.JuspayAction.UPI_TXN.toJsonElement(),
                "orderId" to orderId.toJsonElement(),
                "custVpa" to custVpa.toJsonElement(),
                "showLoader" to false.toJsonElement(),
                "upiSdkPresent" to false.toJsonElement(),
                "clientAuthToken" to clientAuthToken.toJsonElement(),
                "endUrls" to endUrls
            )
        )

        return JsonObject(
            mapOf(
                "requestId" to requestId.toJsonElement(),
                "service" to BuildKonfig.JUSPAY_SERVICE.toJsonElement(),
                "payload" to innerPayload
            )
        )
    }

    fun toMap(): Map<String, Any> {
        val endUrls = listOf(endUrl.toRegex().toString())

        val innerPayload = mapOf(
            "action" to OneTimePaymentConstants.JuspayAction.UPI_TXN,
            "orderId" to orderId,
            "custVpa" to custVpa,
            "showLoader" to false,
            "upiSdkPresent" to false,
            "clientAuthToken" to clientAuthToken,
            "endUrls" to endUrls
        )

        return mapOf(
            "requestId" to requestId,
            "service" to BuildKonfig.JUSPAY_SERVICE,
            "payload" to innerPayload
        )
    }
}