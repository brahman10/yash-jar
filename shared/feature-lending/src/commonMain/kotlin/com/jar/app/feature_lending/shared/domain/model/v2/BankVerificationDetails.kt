package com.jar.app.feature_lending.shared.domain.model.v2

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class BankVerificationDetails(
//    @SerialName("accountHolderName")
//    val accountHolderName: String? = null,
    @SerialName("accountNumber")
    val accountNumber: String? = null,
//    @SerialName("accountType")
//    val accountType: String? = null,
    @SerialName("bankName")
    val bankName: String? = null,
    @SerialName("ifsc")
    val ifsc: String? = null,
    @SerialName("bankLogo")
    val bankLogo: String? = null,
    @SerialName("status")
    val status: String? = null
) : Parcelable