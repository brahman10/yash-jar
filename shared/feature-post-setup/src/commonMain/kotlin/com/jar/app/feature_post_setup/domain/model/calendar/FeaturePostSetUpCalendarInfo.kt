package com.jar.app.feature_post_setup.domain.model.calendar

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class FeaturePostSetUpCalendarInfo(
    @SerialName("id")
    val id: String? = null,

    @SerialName("day")
    val day: Int,

    @SerialName("status")
    val status: String,

    @SerialName("amount")
    val amount: Float? = null,

    @SerialName("isSelected")
    var isSelected: Boolean? = null,

    @SerialName("ladderingPresent")
    val isLadderingPresent: Boolean? = null,
)

