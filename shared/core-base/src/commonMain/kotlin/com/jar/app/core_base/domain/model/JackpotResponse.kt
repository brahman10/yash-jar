package com.jar.app.core_base.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class JackpotResponse(
    @SerialName("outcomeType") val outcomeType: String?,
    @SerialName("header") val header: String?,
    @SerialName("previewImage") val previewImage: String?,
    @SerialName("spinCouponMetadata") val spinCouponMetadata: SpinCouponMetadata?,
    @SerialName("validForText") val validForText: String?,
    @SerialName("redeemCouponCta") val redeemCouponCta: ButtonDetails?,
    @SerialName("spinAgainCta") val spinAgainCta: ButtonDetails?
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class SpinCouponMetadata(
    @SerialName("message") val message: String?,
    @SerialName("couponName") val couponName: String?,
    @SerialName("title") val title: String?,
    @SerialName("description") val description: String?,
    @SerialName("validity") val validity: Long?
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class RedeemCouponCta(
    @SerialName("text") val text: String?,
    @SerialName("deeplink") val deeplink: String?
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class ButtonDetails(
    @SerialName("deeplink")
    val deeplink: String?,
    @SerialName("iconLink")
    val iconLink: String?,
    @SerialName("text")
    val text: String?
) : Parcelable


@kotlinx.serialization.Serializable
@Parcelize
data class JackPotResponseV2(
    @SerialName("rewardType")
    val rewardType: String,
    @SerialName("spinBrandCouponOutcomeResponse")
    val spinBrandCouponOutcomeResponse: SpinBrandCouponOutcomeResponse?,
    @SerialName("spinJarCouponOutcomeResponse")
    val spinJarCouponOutcomeResponse: JackpotResponse?
): Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class ExternalBrandCouponInfo(
    @SerialName("brandCouponCodeId")
    val brandCouponCodeId: String?,
    @SerialName("brandIconLink")
    val brandIconLink: String?,
    @SerialName("brandName")
    val brandName: String?,
    @SerialName("couponState")
    val couponState: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("expiry")
    val expiry: Long?,
    @SerialName("title")
    val title: String?
):Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class SpinBrandCouponOutcomeResponse(
    @SerialName("externalBrandCouponInfo")
    val externalBrandCouponInfo: ExternalBrandCouponInfo?,
    @SerialName("header")
    val header: String?,
    @SerialName("navigationText")
    val navigationText: String,
    @SerialName("shareCta")
    val shareCta: ButtonDetails?,
    @SerialName("shareMsg")
    val shareMsg: String?,
    @SerialName("spinAgainCta")
    val spinAgainCta: ButtonDetails?
):Parcelable