package com.jar.app.feature_lending.shared.domain.model.realTimeFlow

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequest(
    @SerialName("password")
    val password:String,
    @SerialName("submitBankStatements")
    val submitBankStatements:Boolean
)
