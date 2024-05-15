package com.jar.app.feature_transaction.shared.domain.model

import com.jar.app.core_base.domain.model.IconBackgroundTextComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserWinningDetailsRes(
    @SerialName("myWinningsAmount")
    val myWinningsAmount: Float? = null,
    @SerialName("totalWinningsReceivedAmount")
    val totalWinningsReceivedAmount: Float? = null,
    @SerialName("totalWinningsUsedAmount")
    val totalWinningsUsedAmount: Float? = null,
    @SerialName("showChevron")
    val showChevron: Boolean? = null,
    @SerialName("winningsTabActionCTA")
    val winningsTabActionCTA: WinningsTabActionCTA? = null,
    @SerialName("winningsExpiryDesc")
    val winningsExpiryDesc: IconBackgroundTextComponent? = null
)

@Serializable
data class WinningsTabActionCTA(
    @SerialName("ctaText")
    val ctaText: String? = null,
    @SerialName("iconLink")
    val iconLink: String? = null,
    @SerialName("deepLink")
    val deepLink: String? = null
)