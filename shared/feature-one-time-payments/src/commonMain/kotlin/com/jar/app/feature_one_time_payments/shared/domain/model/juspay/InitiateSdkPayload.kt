package com.jar.app.feature_one_time_payments.shared.domain.model.juspay

import com.jar.app.core_base.util.toJsonElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonObject

@kotlinx.serialization.Serializable
data class InitiateSdkPayload(
    @SerialName("requestId")
    val requestId: String,

    @SerialName("service")
    val service: String,

    @SerialName("action")
    val action: String,

    @SerialName("merchantId")
    val merchantId: String,

    @SerialName("clientId")
    val clientId: String,

    @SerialName("customerId")
    val customerId: String,

    @SerialName("environment")
    val environment: String,
) {
    fun toJsonObject(): JsonObject {

        val innerPayload = JsonObject(
            mapOf(
                "action" to action.toJsonElement(),
                "merchantId" to merchantId.toJsonElement(),
                "clientId" to clientId.toJsonElement(),
                "customerId" to customerId.toJsonElement(),
                "environment" to environment.toJsonElement(),
            )
        )

        return JsonObject(
            mapOf(
                "requestId" to requestId.toJsonElement(),
                "service" to service.toJsonElement(),
                "payload" to innerPayload
            )
        )
    }

    fun toMap(): Map<String, Any> {
        val innerPayload = mapOf(
            "action" to action,
            "merchantId" to merchantId,
            "clientId" to clientId,
            "customerId" to customerId,
            "environment" to environment,
        )
        return mapOf(
            "requestId" to requestId,
            "service" to service,
            "payload" to innerPayload
        )
    }
}