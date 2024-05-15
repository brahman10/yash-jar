package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LoanApplicationItemV2(
    @SerialName("applicationId")
    val applicationId: String? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("lender")
    val lender: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("readyCashName")
    val readyCashName: String? = null,
    @SerialName("paidEMI")
    val paidEMI: Int? = null,
    @SerialName("totalEMI")
    val totalEMI: Int? = null,
    @SerialName("paidLoanAmount")
    val paidLoanAmount: Float? = null,
    @SerialName("totalLoanAmount")
    val totalLoanAmount: Float? = null,
    @SerialName("repaymentPercentage")
    val repaymentPercentage: Float? = null
)