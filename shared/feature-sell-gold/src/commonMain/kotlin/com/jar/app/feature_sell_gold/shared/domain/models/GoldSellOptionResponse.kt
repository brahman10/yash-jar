package com.jar.app.feature_sell_gold.shared.domain.models

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class GoldSellOptionResponse(
    @SerialName("allowedLockedAmountINR")
    val allowedLockedAmountINR: Float? = null,
    @SerialName("availableToSellVolume")
    val availableToSellVolume: Float? = null,
    @SerialName("availableToSellAmount")
    val availableToSellAmount: Float? = null,
    @SerialName("goldLockingMessage")
    val goldLockingMessage: String? = null,
    @SerialName("isGoldLocked")
    val isGoldLocked: Boolean? = null,
    @SerialName("lockedVolume")
    val lockedVolume: Float? = null
) : Parcelable