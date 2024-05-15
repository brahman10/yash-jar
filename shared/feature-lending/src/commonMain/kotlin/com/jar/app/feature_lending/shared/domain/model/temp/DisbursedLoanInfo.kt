package com.jar.app.feature_lending.shared.domain.model.temp

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class DisbursedLoanInfo(
    @SerialName("id")
    val id: String,

    @SerialName("accountNo")
    val accountNo: String,

    @SerialName("principal")
    val principal: Float
): Parcelable