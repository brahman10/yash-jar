package com.jar.app.feature_health_insurance.shared.data.models.select_premium

import com.jar.app.core_base.domain.model.card_library.TextData
import com.jar.app.feature_health_insurance.shared.data.models.landing.Benefits
import com.jar.app.feature_health_insurance.shared.data.models.landing.BenefitsDetails
import com.jar.app.feature_health_insurance.shared.data.models.landing.BenefitsDetailsV2
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PremiumOption(
    @SerialName("discountPercTxt")
    val discountPercTxt: String?,
    @SerialName("discountPriceTxt")
    val discountPriceTxt: String?,
    @SerialName("discountedPrice")
    val discountedPrice: Double?,
    @SerialName("gstTxt")
    val gstTxt: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("ctaText")
    val ctaText: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("originalPrice")
    val originalPrice: Int?,
    @SerialName("originalPriceTxt")
    val originalPriceTxt: String?,
    @SerialName("premiumTypeTxt")
    val premiumTypeTxt: String?,
    @SerialName("savingsTxtV2")
    val newSavingTxt: List<TextData>?,
    @SerialName("yearlyCalcTxt")
    val yearlyCalcTxt: String?,
    @SerialName("selected")
    val selected: Boolean?,
    @SerialName("renewalDetails")
    val renewalDetails: BenefitsDetails?
)