package com.jar.app.feature_weekly_magic_common.shared.domain.model

import com.jar.app.core_base.util.orZero
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class WeeklyChallengeMetaData(
    @SerialName("cardsWon")
    val cardsWon: Int? = null,
    @SerialName("targetCards")
    val totalCards: Int? = null,
    @SerialName("isOnBoardedStoryViewed")
    val userOnboarded: Boolean? = null,
    @SerialName("resultViewed")
    val challengeCompletedViewed: Boolean? = null, //update with isChallengeResultViewed API
    @SerialName("challengeId")
    val challengeId: String? = null,
    @SerialName("challengeName")
    val challengeName: String? = null,
    @SerialName("lastTxnAmount")
    val lastTxnAmount: Float? = null,
    @SerialName("currentWeekNumber")
    val currentWeekNumber: Int? = null,
    @SerialName("rewardAmount")
    val rewardAmount: Double? = null,
    @SerialName("prevWeekChallengeId")
    val prevWeekChallengeId: String? = null,
    @SerialName("prevWeekChallengeStoryViewedStatus")
    val prevWeekStoryViewedStatus: Boolean? = null,
    @SerialName("nextChallengeStartDate")
    val nextChallengeStartDate: String? = null,
    @SerialName("description1")
    val postSuccessCardMessage: String? = null,
    @SerialName("description2")
    val postSuccessCardMessage2: String? = null,
    @SerialName("banner")
    val postSuccessBannerMessage: String? = null,
    @SerialName("transactionType")
    val transactionType: String? = null,
    @SerialName("potentialWinAmount")
    val potentialWinAmount: Float? = null,
    @SerialName("cardsLeft")
    val cardsLeft: Int? = null
) : Parcelable{
    fun showWinAnimation(savedCardsWonValue:Int, savedChallengeId:String):Boolean{
        return !challengeId.isNullOrBlank()
                && challengeCompletedViewed != true
                && totalCards.orZero() != 0
                && cardsWon.orZero() != 0
                && (cardsWon.orZero() > savedCardsWonValue || challengeId != savedChallengeId)
    }
}