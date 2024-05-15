package com.jar.feature_gold_price_alerts.shared.domain.model


import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SaveGoldCta(
    @SerialName("title")
    val title: String? = null,
    @SerialName("deepLink")
    val deepLink: String? = null,
    @SerialName("isEnabled")
    val isEnabled: Boolean? = null,
    @SerialName("activeAlertsExists")
    val activeAlertsExists: Boolean? = null,
    @SerialName("iconAnim")
    private val iconAnim: String? = null,
    @SerialName("expandedTitle")
    val expandedTitle: String? = null,
    @SerialName("showShimmer")
    val showShimmer: Boolean? = null,
    @SerialName("iconUrl")
    val iconUrl: String? = null
) {
    fun getIconAnimationType() = SaveGoldCtaAnimType.values().find { it.name == iconAnim } ?: SaveGoldCtaAnimType.NONE
}

enum class SaveGoldCtaAnimType{
    SWING,
    BLINK,
    BLINK_ONCE,
    NONE
}