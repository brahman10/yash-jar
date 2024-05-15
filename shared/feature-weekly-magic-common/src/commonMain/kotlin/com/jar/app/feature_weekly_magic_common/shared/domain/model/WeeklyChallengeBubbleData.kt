package com.jar.app.feature_weekly_magic_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
@Parcelize
data class WeeklyChallengeBubbleData(
    @SerialName("text")
    val text: String,
    @SerialName("duration")
    val duration: Long? = null
): Parcelable