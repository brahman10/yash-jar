package com.jar.app.feature_lending.shared.domain.model.realTimeFlow

import com.jar.app.feature_lending.shared.domain.model.v2.BankAccount
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BankStatement(
    @SerialName("name")
    val name: String,
    @SerialName("sizeInBytes")
    val sizeInBytes: Long,
    @SerialName("fileUrl")
    val fileUrl: String

)

@Serializable
data class BankStatementResponse(
    @SerialName("bankDetail")
    val bankDetail: BankAccount? = null,
    @SerialName("bankStatements")
    val bankStatements: List<BankStatement>? = null
)