package com.jar.app.feature_one_time_payments.shared.domain.model.juspay

import com.jar.app.core_base.util.toJsonElement
import com.jar.app.feature_one_time_payments.shared.BuildKonfig
import com.jar.app.feature_one_time_payments.shared.util.OneTimePaymentConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

@kotlinx.serialization.Serializable
data class InitiateSavedCardPaymentPayload(

    @SerialName("requestId")
    val requestId: String,

    @SerialName("orderId")
    val orderId: String,

    @SerialName("endUrls")
    val endUrl: String,

    @SerialName("paymentMethod")
    val paymentMethod: String,

    @SerialName("cardToken")
    val cardToken: String,

    @SerialName("cardSecurityCode")
    val cardSecurityCode: String,

    @SerialName("clientAuthToken")
    val clientAuthToken: String,

    @SerialName("showLoader")
    val showLoader: Boolean,
) {
    fun toJsonObject(): JsonObject {

        val endUrls = JsonArray(
            listOf(
                endUrl.toRegex().toString().toJsonElement()
            )
        )

        val innerPayload = JsonObject(
            mapOf(
                "action" to OneTimePaymentConstants.JuspayAction.CARD_TXN.toJsonElement(),
                "orderId" to orderId.toJsonElement(),
                "paymentMethod" to paymentMethod.toJsonElement(),
                "cardToken" to cardToken.toJsonElement(),
                "cardSecurityCode" to cardSecurityCode.toJsonElement(),
                "clientAuthToken" to clientAuthToken.toJsonElement(),
                "showLoader" to showLoader.toJsonElement(),
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
            "action" to OneTimePaymentConstants.JuspayAction.CARD_TXN,
            "orderId" to orderId,
            "paymentMethod" to paymentMethod,
            "cardToken" to cardToken,
            "cardSecurityCode" to cardSecurityCode,
            "clientAuthToken" to clientAuthToken,
            "showLoader" to showLoader,
            "endUrls" to endUrls
        )

        return mapOf(
            "requestId" to requestId,
            "service" to BuildKonfig.JUSPAY_SERVICE,
            "payload" to innerPayload
        )
    }
}