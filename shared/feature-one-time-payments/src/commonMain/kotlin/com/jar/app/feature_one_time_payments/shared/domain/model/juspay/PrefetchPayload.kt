package com.jar.app.feature_one_time_payments.shared.domain.model.juspay

import com.jar.app.core_base.util.toJsonElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonObject

@kotlinx.serialization.Serializable
data class PrefetchPayload(
    @SerialName("service")
    val service: String,
    @SerialName("clientId")
    val clientId: String,
) {

    fun toJsonObject(): JsonObject {
        val innerPayload = JsonObject(
            mapOf(
                "clientId" to clientId.toJsonElement()
            )
        )

        return JsonObject(
            mapOf(
                "payload" to innerPayload,
                "service" to service.toJsonElement()
            )
        )
    }

    fun toMap(): Map<String, Any> {
        val innerPayload = mapOf(
            "clientId" to clientId
        )

        return mapOf(
            "payload" to innerPayload,
            "service" to service
        )
    }
}