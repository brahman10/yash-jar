package com.jar.app.feature_user_api.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PauseStatusDTO(
    @SerialName("pausedFor")
    val pausedFor: String? = null,
    @SerialName("pausedOn")
    val pausedOn: String? = null,
    @SerialName("savingsPaused")
    val savingsPaused: Boolean,
    @SerialName("showAlertToUser")
    val showAlertToUser: Boolean,
    @SerialName("unPausedOn")
    val unPausedOn: String? = null,
    @SerialName("willResumeOn")
    val willResumeOn: Long? = null
)