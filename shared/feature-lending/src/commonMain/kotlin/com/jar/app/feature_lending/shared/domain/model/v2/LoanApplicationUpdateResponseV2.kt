package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LoanApplicationUpdateResponseV2(
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("emailId")
    val emailId: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("lender")
    val lender: String? = null,
    @SerialName("lenderApplicationId")
    val lenderApplicationId: String? = null,
    @SerialName("pinCode")
    val pinCode: Int? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("updatedAt")
    val updatedAt: String? = null,
    @SerialName("userId")
    val userId: String? = null
)