package com.jar.app.feature_lending.shared.domain.model.temp

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName

@Parcelize
@kotlinx.serialization.Serializable
data class BankAccountDetails(
    @SerialName("accountHolderName")
    val accountHolderName: String? = null,
    @SerialName("accountNumber")
    val accountNumber: String? = null,
    @SerialName("accountType")
    val accountType: String? = null,
    @SerialName("bankName")
    val bankName: String? = null,
    @SerialName("ifsc")
    val ifsc: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("attemptsRemaining")
    val attemptsRemaining: Int? = null
) : Parcelable