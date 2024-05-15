package com.jar.app.feature_one_time_payments.shared.domain.model.juspay

import com.jar.app.core_base.util.toJsonElement
import com.jar.app.feature_one_time_payments.shared.BuildKonfig
import com.jar.app.feature_one_time_payments.shared.util.OneTimePaymentConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonObject

@kotlinx.serialization.Serializable
data class InitiateUpiIntent(
    @SerialName("requestId")
    val requestId: String,

    @SerialName("orderId")
    val orderId: String,

    @SerialName("payWithApp")
    val payWithApp: String,

    @SerialName("clientAuthToken")
    val clientAuthToken: String,
) {
    fun toJsonObject(): JsonObject {

        val innerPayload = JsonObject(
            mapOf(
                "action" to OneTimePaymentConstants.JuspayAction.UPI_TXN.toJsonElement(),
                "orderId" to orderId.toJsonElement(),
                "payWithApp" to payWithApp.toJsonElement(),
                "clientAuthToken" to clientAuthToken.toJsonElement(),
                "showLoader" to true.toJsonElement()
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
        val innerPayload = mapOf(
            "action" to OneTimePaymentConstants.JuspayAction.UPI_TXN,
            "orderId" to orderId,
            "payWithApp" to payWithApp,
            "clientAuthToken" to clientAuthToken,
            "showLoader" to true
        )
        return mapOf(
            "requestId" to requestId,
            "service" to BuildKonfig.JUSPAY_SERVICE,
            "payload" to innerPayload
        )
    }
}