package com.jar.app.feature_post_setup.domain.model.calendar

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LadderData(
    @SerialName("ladderingOn")
    val ladderingOn: Boolean,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String
)
