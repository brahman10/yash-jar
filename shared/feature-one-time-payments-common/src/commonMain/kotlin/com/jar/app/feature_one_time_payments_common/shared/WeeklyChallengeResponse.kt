package com.jar.app.feature_one_time_payments_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class WeeklyChallengeResponse(
    @SerialName("cardsWon")
    val cardsWon: Int? = null,
    @SerialName("uptoRewardAmount")
    val uptoRewardAmount: Int? = null,
    @SerialName("challengeId")
    val challengeId: String? = null,
    @SerialName("description1")
    val description1: String? = null,
    @SerialName("description2")
    val description2: String? = null,
    @SerialName("banner")
    val banner: String? = null,
    @SerialName("challengeCompleted")
    val challengeCompleted: Boolean? = null,
    @SerialName("weeklyChallengeFlow")
    val weeklyChallengeFlow: Boolean = false
) : Parcelable