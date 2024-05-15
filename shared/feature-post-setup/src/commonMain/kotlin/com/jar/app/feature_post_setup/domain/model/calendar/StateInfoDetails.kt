package com.jar.app.feature_post_setup.domain.model.calendar

data class StateInfoDetails(
    val yearAndMonthText: String,
    val successInfo: AmountInfo?,
    val failureInfo: AmountInfo?,
    val pendingInfo: AmountInfo?
)