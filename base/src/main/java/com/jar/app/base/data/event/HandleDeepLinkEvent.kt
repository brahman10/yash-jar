package com.jar.app.base.data.event

data class HandleDeepLinkEvent(
    val deepLink: String,
    val fromScreen: String? = null,
    val fromSection: String? = null,
    val fromCard: String? = null
)