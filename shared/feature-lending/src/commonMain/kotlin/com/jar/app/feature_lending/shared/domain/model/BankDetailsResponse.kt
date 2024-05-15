package com.jar.app.feature_lending.shared.domain.model

import com.jar.app.feature_lending.shared.domain.model.temp.BankAccountDetails
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BankDetailsResponse(
    @SerialName("bankDetails")
    val bankDetails: BankAccountDetails
)