package com.jar.app.core_ui.generic_post_action.data

data class GenericPostActionDialogData(
    val postActionStatus: String,
    val title: String,
    val analyticsEventName: String?,
    val dialogVisibilityDuration: Long = 3000
)