package com.jar.app.feature_spin.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class ActiveGame(
    @SerialName("createdAt") val createdAt: String?= null,
    @SerialName("updatedAt") val updatedAt: String?= null,
    @SerialName("id") val id: String?= null,
    @SerialName("game") val game: String?= null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class TotalSpinsCta(
    @SerialName("text") val text: String?= null,
    @SerialName("value") val value: Int?= null,
    @SerialName("iconLink") val iconLink: String?= null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class TotalWinningsCta(
    @SerialName("text") val text: String?= null,
    @SerialName("value") val value: Int?= null,
    @SerialName("iconLink") val iconLink: String?= null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class TodayWinnings(
    @SerialName("text") val text: String?= null,
    @SerialName("value") val value: Int?= null,
    @SerialName("iconLink") val iconLink: String?= null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class SpinAnimationDetailsObject(
    @SerialName("spinDiscAnimationDuration") val spinDiscAnimationDuration: Int?= null,
    @SerialName("spinDiscFreeDuration") val spinDiscFreeDuration: Int?= null,
    @SerialName("totalWinningsAnimationDuration") val totalWinningsAnimationDuration: Int?= null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class SpinToWinResponse(
    @SerialName("header") val header: String?= null,
    @SerialName("activeGames") val activeGames: List<ActiveGame>?= null,
    @SerialName("totalSpinsCta") val totalSpinsCta: TotalSpinsCta?= null,
    @SerialName("totalWinningsCta") val totalWinningsCta: TotalWinningsCta?= null,
    @SerialName("areSpinsExhausted") val areSpinsExhausted: Boolean?= null,
    @SerialName("spinsPerDayLimit") val spinsPerDayLimit: Int?= null,
    @SerialName("spinsRemainingToday") val spinsRemainingToday: Int?= null,
    @SerialName("dailySpinsLeftText") val dailySpinsLeftText: String?= null,
    @SerialName("spinsOverMessageObject") val spinsOverMessageObject: SpinOverObject?= null, // can be null
    @SerialName("showSpinsOverMessage") val showSpinsOverMessage: Boolean?= null,
    @SerialName("todayWinnings") val todayWinnings: TodayWinnings?= null,
    @SerialName("showTodayWinnings") val showTodayWinnings: Boolean?= null,
    @SerialName("useWinningsCta") val useWinningsCta: UseWinningCta?= null, // can be null
    @SerialName("showUseWinningsCta") val showUseWinningsCta: Boolean?= null,
    @SerialName("spinAnimationDetailsObject") val spinAnimationDetailsObject: SpinAnimationDetailsObject?= null,
    @SerialName("spinGameBottomSheet") val spinGameBottomSheet: QuestsSpinBackBottomSheet? = null
) : Parcelable


@Parcelize
@kotlinx.serialization.Serializable
data class QuestsSpinBackBottomSheet(
    @SerialName("header") val header: String,
    @SerialName("spinsRemainingIcons") val spinsRemainingIcons: List<String>,
    @SerialName("description") val description: String,
    @SerialName("button1") val button1: ButtonCTA,
    @SerialName("button2") val button2: ButtonCTA,
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class ButtonCTA(
    @SerialName("text") val text: String,
    @SerialName("iconLink") val iconLink: String? = null,
    @SerialName("deeplink") val deeplink: String? = null,
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class SpinOverObject(
    @SerialName("message") val message: String?= null,
    @SerialName("iconLink") val iconLink: String?= null,
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class UseWinningCta(
    @SerialName("text") val text: String?= null,
    @SerialName("deeplink") val deeplink: String?= null,
) : Parcelable