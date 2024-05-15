package com.jar.app.feature_one_time_payments_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PostPaymentRewardCard(
    @SerialName("animationType")
    val animationType: String?,
    @SerialName("deepLink")
    val deepLink: String?,
    @SerialName("bannerText")
    val bannerText: String?,
    @SerialName("secondaryTitle")
    val secondaryTitle: String?,
    @SerialName("tertiaryTitle")
    val tertiaryTitle: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("bannerImage")
    val bannerImage: String?,
    @SerialName("lottieUrl")
    val lottieUrl: String?,
    @SerialName("ctaText")
    val ctaText: String?,
    @SerialName("targetCards")
    val targetCards: Int?,
    @SerialName("cardsWon")
    val cardsWon: Int?,
    @SerialName("daysLeft")
    val daysLeft:Int?,
    @SerialName("subtitle") val subtitle: String?,
    @SerialName("boundaryColor") val boundaryColor: String?,
    @SerialName("trophyImage") val trophyImage: String?,
) : Parcelable