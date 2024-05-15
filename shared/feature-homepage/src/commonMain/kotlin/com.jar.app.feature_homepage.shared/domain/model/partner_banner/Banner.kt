package com.jar.app.feature_homepage.shared.domain.model.partner_banner

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Banner(
    @SerialName("id")
    val partnerId: String,

    @SerialName("type")
    val type: String,

    @SerialName("campaignId")
    val campaignId: String,

    @SerialName("partner")
    val partner: String,

    @SerialName("partnerLogo")
    val partnerLogo: String,

    @SerialName("title")
    val title: String,

    @SerialName("description")
    val description: String,

    @SerialName("orderId")
    val orderId: String
) {
    fun getUniqueKey() = orderId
}