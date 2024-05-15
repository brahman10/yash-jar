package com.jar.app.feature_refer_earn_v2.shared.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReferralProgress(
    @SerialName("attributionTime")
    val attributionTime: String? = null,
    @SerialName("completed")
    val completed: Boolean? = null,
    @SerialName("delayInMinutes")
    val delayInMinutes: String? = null,
    @SerialName("deepLink")
    val deeplink: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("key")
    val key: String? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("value")
    val value: String? = null,
) {
    fun isNullTransaction(): Boolean {
        return (value.isNullOrEmpty())
    }
}