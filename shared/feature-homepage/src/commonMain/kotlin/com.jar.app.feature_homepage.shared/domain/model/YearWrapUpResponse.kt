package com.jar.app.feature_homepage.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
class YearWrapUpResponse(
    @SerialName("totalSavingsInCurrency")
    val totalSavingsInCurrency: Float,
    @SerialName("totalSavingsInVolume")
    val totalSavingsInVolume: Float,
    @SerialName("totalDays")
    val totalDays: Int,
    @SerialName("totalSpins")
    val totalSpins: Int,
    @SerialName("spinsLeft")
    val spinsLeft: Int,
    @SerialName("earnUpToBySpin")
    val earnUpToBySpin: Float,
    @SerialName("totalWonAmountInFreeGold")
    val totalWonAmountInFreeGold: Float,
    @SerialName("currentAutoSaveAmount")
    val currentAutoSaveAmount: Float,
    @SerialName("extraAutoSaveAmount")
    val extraAutoSaveAmount: Float,
    @SerialName("earnUpToByAutoSave")
    val earnUpToByAutoSave: Float,
    @SerialName("showAutoSaveOption")
    val showAutoSaveOption: Boolean
) : Parcelable