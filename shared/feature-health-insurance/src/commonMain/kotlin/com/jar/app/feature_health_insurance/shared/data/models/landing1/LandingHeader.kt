package com.jar.app.feature_health_insurance.shared.data.models.landing1


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LandingHeader(
    @SerialName("familyImage")
    val familyImage: String? = null,
    @SerialName("shieldIcon")
    val shieldIcon: String? = null,
    @SerialName("faqIcon")
    val faqIcon: String? = null,
    @SerialName("faqText")
    val faqText: String? = null,
    @SerialName("header")
    val header: String? = null,
    @SerialName("infoIcon")
    val infoIcon: String? = null,
    @SerialName("infoText")
    val infoText: String? = null,
    @SerialName("subHeader")
    val subHeader: String? = null,
    @SerialName("subHeaderNew")
    val subHeaderNew: String? = null,
    @SerialName("partnerText")
    val partnerText: String? = null,
    @SerialName("partnerLogo")
    val partnerLogo: String? = null,
    @SerialName("infoTextNew")
    val infoTextNew: String? = null,
    @SerialName("infoIconEnd")
    val infoIconEnd: String? = null,
    @SerialName("defaultVideo")
    val defaultVideo: DefaultVideo? = null
)