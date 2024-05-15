package com.jar.app.feature_lending_kyc.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class CreditReportPAN(
    @SerialName("panNumber")
    val panNumber: String,

    @SerialName("firstName")
    val firstName: String,

    @SerialName("lastName")
    val lastName: String,

    @SerialName("dob")
    val dob: String
) : Parcelable