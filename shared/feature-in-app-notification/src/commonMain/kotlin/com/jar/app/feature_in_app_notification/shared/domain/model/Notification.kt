package com.jar.app.feature_in_app_notification.shared.domain.model

data class Notification(
    val id: String?,

    val userId: String?,

    val title: String?,

    val description: String?,

    val callToAction: String?,

    val createdAt: String?,

    val createdAtUtc: Long?,

    val icon: String?,

    val deepLink: String?,

    val seen: Boolean?,

    val category: String?
)