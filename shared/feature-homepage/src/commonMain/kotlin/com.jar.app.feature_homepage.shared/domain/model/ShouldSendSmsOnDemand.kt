package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShouldSendSmsOnDemand(
    @SerialName("sendSmsData")
    val sendSms: Boolean,
    @SerialName("numberOfDays")
    val numberOfDays: Int
)