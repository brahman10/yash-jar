package com.jar.app.feature_health_insurance.shared.data.models.select_premium

import com.jar.app.core_base.domain.model.card_library.TextData
import com.jar.app.feature_health_insurance.shared.data.models.landing1.AbandonScreenBenefits
import com.jar.app.feature_health_insurance.shared.data.models.landing1.BenefitsX
import com.jar.app.feature_health_insurance.shared.data.models.landing1.ComparisonFields
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SelectPlanReturnScreen(
    @SerialName("benefitsList")
    val benefitsList: List<AbandonScreenBenefits>? = null,
    @SerialName("comparisonFieldsList")
    val comparisonFieldsList: List<ComparisonFields>? = null,
    @SerialName("exitCTA")
    val exitCTA: String? = null,
    @SerialName("exitCTAText")
    val exitCTAText: String? = null,
    @SerialName("getQuoteCTA")
    val getQuoteCTA: String? = null,
    @SerialName("getQuoteCTAText")
    val getQuoteCTAText: String? = null,
    @SerialName("headerText")
    val headerText: List<TextData>? = null,
    @SerialName("subText")
    val subText: List<TextData>? = null,
    @SerialName("crossIcon")
    val crossIcon: String? = null
)