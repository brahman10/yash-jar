package com.jar.app.feature_user_api.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class WinningResponse(
    @SerialName("winningsAmount")
    val winningsAmount: Double,

    @SerialName("minWinningWithdrawAmount")
    val minWinningWithdrawAmount: Double,

    @SerialName("winningLimitMessage")
    val winningLimitMessage: String? = null,
) : Parcelable