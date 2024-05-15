package com.jar.app.feature_kyc.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class ManualKycRequest(
    @SerialName("panNumber")
    val panNumber: String,

    @SerialName("dob")
    val dob: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("emailId")
    val emailId: String? = null
) : Parcelable