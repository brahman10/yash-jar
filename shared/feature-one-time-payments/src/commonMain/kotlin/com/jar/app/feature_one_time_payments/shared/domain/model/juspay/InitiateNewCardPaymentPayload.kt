package com.jar.app.feature_one_time_payments.shared.domain.model.juspay

import com.jar.app.core_base.util.toJsonElement
import com.jar.app.feature_one_time_payments.shared.BuildKonfig
import com.jar.app.feature_one_time_payments.shared.util.OneTimePaymentConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

@kotlinx.serialization.Serializable
data class InitiateNewCardPaymentPayload(

    @SerialName("requestId")
    val requestId: String,

    @SerialName("orderId")
    val orderId: String,

    @SerialName("endUrls")
    val endUrl: String,

    @SerialName("paymentMethod")
    val paymentMethod: String,

    @SerialName("cardNumber")
    val cardNumber: String,

    @SerialName("nameOnCard")
    val nameOnCard: String,

    @SerialName("cardExpMonth")
    val cardExpMonth: String,

    @SerialName("cardExpYear")
    val cardExpYear: String,

    @SerialName("cardSecurityCode")
    val cardSecurityCode: String,

    @SerialName("saveToLocker")
    val saveToLocker: Boolean,

    @SerialName("clientAuthToken")
    val clientAuthToken: String,

    @SerialName("showLoader")
    val showLoader: Boolean,
) {

    fun toJsonObject(): JsonObject {

        val endUrls = JsonArray(listOf(endUrl.toRegex().toString().toJsonElement()))

        val innerPayload = JsonObject(
            mapOf(
                "action" to OneTimePaymentConstants.JuspayAction.CARD_TXN.toJsonElement(),
                "orderId" to orderId.toJsonElement(),
                "paymentMethod" to paymentMethod.toJsonElement(),
                "endUrls" to endUrls,
                "cardNumber" to cardNumber.toJsonElement(),
                "cardExpMonth" to cardExpMonth.toJsonElement(),
                "cardExpYear" to cardExpYear.toJsonElement(),
                "cardSecurityCode" to cardSecurityCode.toJsonElement(),
                "saveToLocker" to saveToLocker.toJsonElement(),
                "clientAuthToken" to clientAuthToken.toJsonElement(),
                "nameOnCard" to nameOnCard.toJsonElement()
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
            "endUrls" to endUrls,
            "cardNumber" to cardNumber,
            "cardExpMonth" to cardExpMonth,
            "cardExpYear" to cardExpYear,
            "cardSecurityCode" to cardSecurityCode,
            "saveToLocker" to saveToLocker,
            "clientAuthToken" to clientAuthToken,
            "nameOnCard" to nameOnCard
        )

        return mapOf(
            "requestId" to requestId,
            "service" to BuildKonfig.JUSPAY_SERVICE,
            "payload" to innerPayload
        )
    }
}