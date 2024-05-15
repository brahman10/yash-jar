package com.jar.app.feature_lending.shared.domain.model.v2

//To transfer data to Confirmation screen
@kotlinx.serialization.Serializable
data class BankDataDto(
    val bankLogoUrl: String,
    val bankName: String,
    val accountNumber: String
)