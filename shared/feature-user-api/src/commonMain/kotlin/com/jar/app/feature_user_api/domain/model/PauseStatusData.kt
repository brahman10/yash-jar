package com.jar.app.feature_user_api.domain.model

@kotlinx.serialization.Serializable
data class PauseStatusData(
    val pausedFor: String?,

    val pausedOn: String?,

    var savingsPaused: Boolean,

    val showAlertToUser: Boolean,

    val unPausedOn: String?,

    val willResumeOn: Long?
)