package com.jar.app.feature_health_insurance.shared.data.models.add_details

data class Plan(
    val coverageAmount: Int,
    val coverageTxt: String,
    val name: String,
    val premiumOptions: List<PremiumOption>
)