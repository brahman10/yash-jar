package com.jar.app.feature_lending.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class LoanDetailsRequest(
    @SerialName("applicationId")
    val applicationId: String? = null,

    @SerialName("loanDetails")
    val loanDetails: LoanDetail? = null
) : Parcelable

@Parcelize
@kotlinx.serialization.Serializable
data class LoanDetail(
    @SerialName("applicationStatus")
    val applicationStatus: String? = null,

    @SerialName("disbursedAmt")
    val disbursedAmt: Float? = null,

    @SerialName("disbursedAt")
    val disbursedAt: String? = null,

    @SerialName("emi")
    val emi: Float? = null,

    @SerialName("loanId")
    val loanId: String? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("reason")
    var reason: String? = null,

    @SerialName("roi")
    val roi: Float? = null,

    @SerialName("status")
    val status: String? = null,

    @SerialName("tenure")
    val tenure: Int? = null
) : Parcelable