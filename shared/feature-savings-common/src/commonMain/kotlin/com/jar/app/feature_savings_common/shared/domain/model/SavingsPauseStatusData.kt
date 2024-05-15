package com.jar.app.feature_savings_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class SavingsPauseStatusData(
    @SerialName("pausedFor")
    val pausedFor: String? = null,
    @SerialName("pausedOn")
    val pausedOn: String? = null,
    @SerialName("savingsPaused")
    val savingsPaused: Boolean? = null,
    @SerialName("showAlertToUser")
    val showAlertToUser: Boolean? = null,
    @SerialName("unPausedOn")
    val unPausedOn: String? = null,
    @SerialName("willResumeOn")
    val willResumeOn: Long? = null
) : Parcelable