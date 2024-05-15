package com.jar.app.feature_health_insurance.shared.data.models.select_premium

import com.jar.app.core_base.domain.model.card_library.TextData
import com.jar.app.feature_health_insurance.shared.data.models.landing.Benefits
import com.jar.app.feature_health_insurance.shared.data.models.landing.BenefitsV2
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Plan(
    @SerialName("coverageAmount")
    val coverageAmount: Int?,
    @SerialName("coverageTxt")
    val coverageTxt: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("premiumOptions")
    val premiumOptions: List<PremiumOption?>,
    @SerialName("planBenefits")
    val planBenefits: BenefitsV2?,
    @SerialName("infoText")
    val infoText: List<TextData>? = null,
    @SerialName("infoIcon")
    val infoTextIcon: String? = null
)