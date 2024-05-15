package com.jar.app.feature_health_insurance.shared.data.models.add_details

data class Main(
    val benefitsTxt: String,
    val choosePremium: ChoosePremium,
    val ctaText: String,
    val defaultPlanIndex: Int,
    val defaultPlanTxt: String,
    val partnership: Partnership,
    val plans: List<Plan>
)