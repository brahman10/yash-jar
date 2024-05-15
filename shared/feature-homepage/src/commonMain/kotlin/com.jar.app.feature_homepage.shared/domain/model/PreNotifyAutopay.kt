package com.jar.app.feature_homepage.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PreNotifyAutopay(
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("footerText")
    val footerText: String? = null,
    @SerialName("shouldShowCard")
    val shouldShowCard: Boolean,
    @SerialName("amount")
    val amount: Float? = null,
    @SerialName("preNotificationIdList")
    val preNotificationIds: List<String>
)

enum class PreNotificationDismissalType {
    DISMISS, NEVER_SHOW_AGAIN
}