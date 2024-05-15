package com.jar.app.feature_one_time_payments.shared.domain.model.juspay

import com.jar.app.core_base.util.toJsonElement
import com.jar.app.feature_one_time_payments.shared.BuildKonfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonObject

@kotlinx.serialization.Serializable
internal data class GetPaymentMethods(
    @SerialName("requestId")
    val requestId: String
) {
    fun toJsonObject(): JsonObject {
        val innerPayload = JsonObject(
            mapOf(
                "action" to "getPaymentMethods".toJsonElement()
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
            "action" to "getPaymentMethods"
        )

        return mapOf(
            "requestId" to requestId,
            "service" to BuildKonfig.JUSPAY_SERVICE,
            "payload" to innerPayload
        )

    }
}