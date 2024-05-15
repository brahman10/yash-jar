package com.jar.app.feature_health_insurance.shared.data.models.select_premium

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Main(
    @SerialName("benefitsTxt")
    val benefitsTxt: String?,
    @SerialName("choosePremium")
    val choosePremium: ChoosePremium?,
    @SerialName("ctaText")
    val ctaText: String?,
    @SerialName("defaultPlanIndex")
    val defaultPlanIndex: Int?,
    @SerialName("defaultPlanTxt")
    val defaultPlanTxt: String?,
    @SerialName("partnership")
    val partnership: Partnership?,
    @SerialName("plans")
    val plans: List<Plan?>,
    @SerialName("providerId")
    val providerId: String? = null,
    @SerialName("recommendedPlanIndex")
    val recommendedPlanIndex: Int? = null
)