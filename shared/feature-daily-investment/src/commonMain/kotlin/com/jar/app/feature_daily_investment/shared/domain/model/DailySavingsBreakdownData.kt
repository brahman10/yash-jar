package com.jar.app.feature_daily_investment.shared.domain.model

data class DailySavingsBreakdownData(
    val heading : String,
    val chips : List<String>,
    val subHeading1 : String,
    val breakDownSummary : List<Details>,
    val subHeading2: String,
    val breakDownDetails : List<Details>,
    val gst : String,
    val warning : String,
)
data class Details(
    val label: String,
    val value: String,
    val color: Int,
)
