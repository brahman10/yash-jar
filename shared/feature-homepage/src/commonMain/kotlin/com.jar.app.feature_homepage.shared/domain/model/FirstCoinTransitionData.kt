package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FirstCoinTransitionData(
    @SerialName("title")
    val title: String,

    @SerialName("description")
    val description: String,

    @SerialName("image")
    val image: String,

    @SerialName("btnText")
    val btnText: String?,

    @SerialName("deeplink")
    val deeplink: String? = null,

    @SerialName("footer")
    val footer: String? = null,

    @SerialName("deliveryStatus")
    private val deliveryStatus: String? = null,
) {
    fun getDeliveryStatus(): DeliveryStatus? {
        return if (deliveryStatus.isNullOrEmpty())
            null
        else
            com.jar.app.feature_homepage.shared.domain.model.DeliveryStatus.valueOf(deliveryStatus)
    }

}

enum class DeliveryStatus {
    FAILED, DELIVERED, PENDING
}
