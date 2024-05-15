package com.jar.app.feature_one_time_payments_common.shared

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PostOrderCrossSellCardData(
    @SerialName("infographicUrl")
    val infographicUrl: String,

    @SerialName("primaryText")
    val primaryText: String?, //Will be null if infographic type is FULL_IMAGE

    @SerialName("secondaryText")
    val secondaryText: String?, //Will be null if infographic type is FULL_IMAGE

    @SerialName("primaryCtaText")
    val primaryCtaText: String?, //Will be null if infographic type is FULL_IMAGE

    @SerialName("backgroundColor")
    val backgroundColor: String?, //Will be null if infographic type is FULL_IMAGE

    @SerialName("ctaDeeplink")
    val ctaDeeplink: String,

    @SerialName("ctaBackgroundColor")
    val ctaBackgroundColor: String?, //Will be null if infographic type is FULL_IMAGE

    @SerialName("cardType")
    val cardType: String,

    @SerialName("infographicType")
    private val infographicType: String,
): Parcelable {
    fun getInfographicType() = BuyGoldCrossPromotionInfographicType.values().find { it.name == infographicType }
}

enum class BuyGoldCrossPromotionInfographicType{
    IMAGE, //Show banner as card with image
    LOTTIE, //Show banners as card with lottie
    FULL_IMAGE //Show banner as image
}