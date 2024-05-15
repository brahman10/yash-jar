package com.jar.app.feature_lending.shared.domain.model.repayment

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RepaymentDetailResponse(
    @SerialName("emiDetails")
    val emiDetails: List<RepaymentDetail>? = null,
    @SerialName("isForecloseEnabled")
    val isForecloseEnabled: Boolean? = null,
    @SerialName("paidEMI")
    val paidEMI: Int? = null,
    @SerialName("paidLoanAmount")
    val paidLoanAMount: Float? = null,
    @SerialName("loanStatus")
    val loanStatus: String? = null,
    @SerialName("totalEMI")
    val totalEMI: Int? = null,
    @SerialName("totalLoanAmount")
    val totalLoanAmount: Float? = null
)