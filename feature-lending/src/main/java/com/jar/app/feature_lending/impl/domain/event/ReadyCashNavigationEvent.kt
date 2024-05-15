package com.jar.app.feature_lending.impl.domain.event

data class ReadyCashNavigationEvent(
    val whichScreen: String,
    val source: String,
    val launchType: String? = null,
    val popupToId: Int? = null,
    val isBackFlow: Boolean = false,
    val isRepaymentFlow: Boolean = false,
    val shouldCacheThisEvent: Boolean = true
)
