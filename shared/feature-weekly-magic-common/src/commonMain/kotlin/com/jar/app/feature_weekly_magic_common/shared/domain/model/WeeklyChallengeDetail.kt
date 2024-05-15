package com.jar.app.feature_weekly_magic_common.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class WeeklyChallengeDetail(
    @SerialName("challengeId")
    val challengeId: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("storyTitle")
    val storyTitle: String? = null,
    @SerialName("descriptionUsersParticipated")
    val descriptionUsersParticipated: Int? = null,
    @SerialName("descriptionAmountWon")
    val descriptionAmountWon: Float? = null,
    @SerialName("potentialWinAmount")
    val potentialWinAmount: Float? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("description1")
    val postSuccessCardMessage: String? = null,
    @SerialName("description2")
    val postSuccessCardMessage2: String? = null,
    @SerialName("banner")
    val postSuccessBannerMessage: String? = null,
    @SerialName("descriptions")
    val descriptions: List<String>? = null,   //for Info
    @SerialName("totalNumberofcards")
    val totalNumberofcards: Int? = null,
    @SerialName("numCardsCollected")
    val numCardsCollected: Int? = null,
    @SerialName("minEligibleTxnAmount")
    val minEligibleTxnAmount: Float? = null,
    @SerialName("rewardAmount")
    val rewardAmount: Float? = null,
    @SerialName("prevChallengeId")
    val prevChallengeId: String? = null,
    @SerialName("nextChallengeId")
    val nextChallengeId: String? = null,
    @SerialName("nextChallengeStartDate")
    val nextChallengeStartDate: String? = null,
    @SerialName("infoViewed")
    val isInfoViewed: Boolean? = null,
    @SerialName("resultViewed") //isChallengeResultViewed
    val prevResultViewed: Boolean? = null,
    @SerialName("profileImages")
    val participatedUrls: List<String>? = null,
    @SerialName("currentWeekChallengeViewedStatus")
    val currentWeekChallengeViewedStatus: Boolean? = null,
    @SerialName("ctaRedirectionType")
    val ctaRedirectionType: String? = null,
    @SerialName("ctaDeeplink")
    val ctaDeeplink: String? = null,
    @SerialName("ctaText")
    val ctaText: String? = null,
    @SerialName("daysLeftDescription")
    val daysLeftDescription: String? = null,
    @SerialName("secondaryDescription")
    val secondaryDescription: String? = null,
    @SerialName("primaryDescription")
    val primaryDescription: String? = null,
    @SerialName("chatBubbleList")
    val chatBubbleList: List<WeeklyChallengeBubbleData>? = null,

    //Fields Used for History and Story Screen
    @SerialName("usersParticipatedText")
    val usersParticipatedText: String? = null,
    @SerialName("amountWonText")
    val amountWonText: String? = null,
    @SerialName("bottomStoryText")
    val bottomStoryText: String? = null,
    @SerialName("highlightedText")
    val highlightedText: WeeklyChallengeHighlightedText? = null
) : Parcelable {
    fun getCtaRedirectionType() = WeeklyChallengeCtaRedirectionType.values().find { it.name == ctaRedirectionType } ?: WeeklyChallengeCtaRedirectionType.BUY_GOLD
}