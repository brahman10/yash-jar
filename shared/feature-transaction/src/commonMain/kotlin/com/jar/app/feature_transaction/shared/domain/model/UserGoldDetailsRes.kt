package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.domain.model.GoldBalanceViewType
import com.jar.app.core_base.domain.model.IconBackgroundTextComponent
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserGoldDetailsRes(
    @SerialName("cached")
    val cached: Boolean? = null,
    @SerialName("currentValue")
    val currentValue: Float? = null,
    @SerialName("investedValue")
    val investedValue: Float? = null,
    @SerialName("lastRefreshedAt")
    val lastRefreshedAt: Long? = null,
    @SerialName("showCurrentValue")
    val showCurrentValue: Boolean? = null,
    @SerialName("showInvestedValue")
    val showInvestedValue: Boolean? = null,
    @SerialName("giftingEnabled")
    val giftingEnabled: Boolean? = null,
    @SerialName("unit")
    val unit: String? = null,
    @SerialName("volume")
    val volume: Float? = null,
    @SerialName("unitPreference")
    val unitPreference: String? = null,
    @SerialName("volumeInMg")
    val volumeInMg: Float? = null,
    @SerialName("cta")
    val cta: TransactionPageCTA,
    @SerialName("balanceView")
    val balanceView: GoldBalanceViewType? = GoldBalanceViewType.ONLY_GM,
    @SerialName("investedExtraGold")
    val investedExtraGold: Float? = null,
    @SerialName("jarWinningsFooter")
    val jarWinningsFooter: IconBackgroundTextComponent? = null
)

@kotlinx.serialization.Serializable
data class TransactionPageCTA(
    @SerialName("text")
    val text: String,
    @SerialName("deepLink")
    val deeplink: String,
    @SerialName("icon")
    val icon: String?,
)