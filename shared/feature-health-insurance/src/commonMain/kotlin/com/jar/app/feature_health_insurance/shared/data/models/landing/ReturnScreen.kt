package com.jar.app.feature_health_insurance.shared.data.models.landing

import kotlinx.serialization.Serializable

@Serializable
data class ReturnScreen(
    val benefitsList: List<String>?=null,
    val comparisonFieldsList: List<ComparisonFields>?=null,
    val exitCTA: String?=null,
    val exitCTAText: String?=null,
    val getQuoteCTA: String?=null,
    val getQuoteCTAText: String?=null,
    val headerText: String?=null,
    val subText: String?=null
)