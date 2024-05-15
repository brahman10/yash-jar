package com.jar.app.feature_post_setup.domain.model.calendar

import kotlinx.serialization.SerialName


@kotlinx.serialization.Serializable
data class SavingOperations(
    @SerialName("icon")
    val icon: String,
    @SerialName("title")
    val title: String,
    @SerialName("deepLink")
    val deeplink: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("cardType")
    val cardType: String? = null,
    @SerialName("isPrimary")
    val isPrimary: Boolean? = null,
    @SerialName("quickActionType")
    val quickActionType: String? = null
)
