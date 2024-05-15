package com.jar.app.feature_refer_earn_v2.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReferralShareMessageData(
    @SerialName("whatsAppShareMessage")
    val whatsAppShareMessage: String? = null,
    @SerialName("whatsAppShareImage")
    val whatsAppShareImage: String? = null,
    @SerialName("othersShareMessage")
    val othersShareMessage: String? = null,
)