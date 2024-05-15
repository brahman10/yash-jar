package com.jar.app.base.data.event

data class LendingBackPressEvent(
    val screenName: String,
    val shouldNavigateBack:Boolean = false,
    val popupId:Int?=null,
    val logAnalyticsEvent:Boolean = true
)