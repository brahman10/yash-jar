package com.jar.app.feature_lending.shared.domain.model

import com.jar.app.feature_lending.shared.domain.model.v2.PreApprovedData
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
@kotlinx.serialization.Serializable
data class DrawDownScreenArgs(
    val preApprovedData: PreApprovedData,
    val isFromRepeatWithdrawal: Boolean = false,
    val loanId: String? = null,
    val amount: Float = 0f,
) : Parcelable
