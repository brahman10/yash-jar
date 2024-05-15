package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QnAResponse(
    @SerialName("message")
    val message: String? = null,
    @SerialName("question")
    val question: String? = null,
    @SerialName("options")
    val options: List<String>? = null,
    @SerialName("submitButton")
    val submitButton: SubmitButton? = null,
)

@Serializable
data class SubmitButton(
    @SerialName("text")
    val text: String? = null,
    @SerialName("iconLink")
    val iconLink: String? = null,
    @SerialName("deeplink")
    val deeplink: String? = null
)