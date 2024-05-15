package com.jar.app.core_base.data.dto

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class GoldBalanceDTO(
    @SerialName("cached")
    val isCached: Boolean,

    @SerialName("lastRefreshedAt")
    val lastRefreshedAt: Long,

    @SerialName("balanceView")
    val balanceView: String? = null,

    @SerialName("unit")
    val unit: String,

    @SerialName("volume")
    val volume: Float,

    @SerialName("currentValue")
    val currentValue: Float? = null,

    @SerialName("investedValue")
    val investedValue: Float? = null,

    @SerialName("unitPreference")
    val unitPreference: String? = null,

    @SerialName("volumeInMg")
    val volumeInMg: Float? = null,

    @SerialName("showLeaseBanner")
    val showLeaseBanner: Boolean? = null,

    @SerialName("goldLeaseBreakupObject")
    val goldLeaseBreakupObject: GoldLeaseBreakupObjectDTO? = null,

    @SerialName("lockerView")
    val firstTransactionLockerDataObject: FirstTransactionLockerDataDTO? = null,

    @SerialName("jarWinningsFooter")
    val jarWinningsFooter: JarWinningsFooterDTO? = null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class JarWinningsFooterDTO(
    @SerialName("iconUrl")
    val iconUrl: String? = null,

    @SerialName("bgColor")
    val bgColor: String? = null,

    @SerialName("text")
    val text: String? = null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class GoldLeaseBreakupObjectDTO(
    @SerialName("title")
    val title: String? = null,

    @SerialName("volumeLeased")
    val volumeLeased: Float? = null,

    @SerialName("amountLeased")
    val amountLeased: Float? = null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class FirstTransactionLockerDataDTO(
    @SerialName("backgroundImage")
    val backgroundImage: String? = null,

    @SerialName("header")
    val header: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("subTitle")
    val subTitle: String? = null,

    @SerialName("cta")
    val firstTransactionLockerCtaObject: FirstTransactionLockerCtaDTO? = null,

    @SerialName("showGoldBalanceAnimation")
    val showGoldBalanceAnimation: Boolean? = null,

    @SerialName("txnCount")
    val txnCount: Int? = null,

    @SerialName("variant")
    val variant: String? = null,

    @SerialName("primaryTextColor")
    val primaryTextColor: String? = null,

    @SerialName("secondaryTextColor")
    val secondaryTextColor: String? = null,
) : Parcelable


@Parcelize
@kotlinx.serialization.Serializable
data class FirstTransactionLockerCtaDTO(
    @SerialName("ctaDeeplink")
    val ctaDeeplink: String? = null,
    @SerialName("ctaText")
    val ctaText: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("ctaColor")
    val ctaColor: String? = null,
) : Parcelable