package com.jar.app.feature_health_insurance.shared.data.models.add_details

data class PlanConfigResponse(
    val currentPageNo: Int,
    val header: Header,
    val insuranceType: String,
    val main: Main,
    val pageTitle: String,
    val totalPageNo: Int
)