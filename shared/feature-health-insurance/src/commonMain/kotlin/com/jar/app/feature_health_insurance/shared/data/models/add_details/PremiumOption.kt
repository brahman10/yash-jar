package com.jar.app.feature_health_insurance.shared.data.models.add_details

data class PremiumOption(
    val discountPercTxt: String,
    val discountPriceTxt: String,
    val discountedPrice: Double,
    val gstTxt: String,
    val id: String,
    val name: String,
    val originalPrice: Int,
    val originalPriceTxt: String,
    val premiumTypeTxt: String,
    val savingTxt: String,
    val yearlyCalcTxt: String
)