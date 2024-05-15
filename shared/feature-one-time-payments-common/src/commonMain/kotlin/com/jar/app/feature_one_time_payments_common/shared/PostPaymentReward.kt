package com.jar.app.feature_one_time_payments_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PostPaymentReward(
    @SerialName("header") val header: String?,
    @SerialName("bottomHeader") val bottomHeader: String?,
    @SerialName("postPaymentRewardCardList") val postPaymentRewardCardList: List<PostPaymentRewardCard>? = null,
    @SerialName("bannerText") val bannerText: String?,
    @SerialName("savedText") val savedText: String?,
    @SerialName("subText") val subText: String?,
    @SerialName("goldValue") val goldValue: String?,
    @SerialName("subTitle") val subTitle: String?,
    @SerialName("version") val version: String?, // Assuming version is a string like "v1"
    @SerialName("subtitle") val subtitle: String?,
) : Parcelable