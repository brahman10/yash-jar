package com.jar.app.feature_goal_based_saving.shared.data.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AbandonedScreenResponse(
    @SerialName("description")
    val description: String? = null,
    @SerialName("exitCta")
    val exitCta: ExitCta? = null,
    @SerialName("footerButtonText")
    val footerButtonText: String? = null,
    @SerialName("header")
    val header: String? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("socialProofingText")
    val socialProofingText: String? = null
)

@kotlinx.serialization.Serializable
data class AnsweredDetail(
     @SerialName("answer")
     val answer: String? = null,
     @SerialName("editIcon")
     val editIcon: String? = null
)

@kotlinx.serialization.Serializable
data class ExitCta(
     @SerialName("deeplink")
    val deeplink: String? = null,
     @SerialName("iconLink")
    val iconLink: String? = null,
     @SerialName("text")
    val text: String? = null
)