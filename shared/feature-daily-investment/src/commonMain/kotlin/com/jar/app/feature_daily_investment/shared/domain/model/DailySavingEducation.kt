package com.jar.app.feature_daily_investment.shared.domain.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DailySavingEducation(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("imageUrl")
    val imageUrl: String,
    var isExpanded: Boolean = false,
    var isListIterated: Boolean = false
)
