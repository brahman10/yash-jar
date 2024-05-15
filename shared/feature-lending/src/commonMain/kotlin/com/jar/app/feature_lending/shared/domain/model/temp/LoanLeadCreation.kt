package com.jar.app.feature_lending.shared.domain.model.temp

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class LoanLeadCreation(
    @SerialName("status")
    val status: String? = null,

    @SerialName("responseIdentifier")
    val responseIdentifier: String? = null,

    @SerialName("requestIdentifier")
    val requestIdentifier: String? = null,

    @SerialName("lenderInfo")
    val lenderInfo: LoanLenderInfo? = null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class LoanLenderInfo(
    @SerialName("applicationId")
    val applicationId: String? = null,

    @SerialName("applicantId")
    val applicantId: String? = null

) : Parcelable