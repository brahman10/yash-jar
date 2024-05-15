package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateLoanDetailsBodyV2(
    @SerialName("applicationId")
    val applicationId: String? = null,
    @SerialName("bankVerificationDetails")
    val bankVerificationDetails: BankVerificationDetails? = null,
    @SerialName("emailId")
    val emailId: String? = null,
    @SerialName("ipAddress")
    val ipAddress: String? = null,
    @SerialName("employmentDetails")
    val employmentDetails: EmploymentDetailsBody? = null,
    @SerialName("pinCode")
    val pinCode: Int? = null,
    @SerialName("drawdownDetails")
    val drawdownDetails: DrawdownDetails? = null,
    @SerialName("kycVerificationConsent")
    val kycVerificationConsent: Boolean? = null,
    @SerialName("mandateDetails")
    val mandateDetails: MandateDetailsV2? = null,
    @SerialName("readyCashSubmitDetails")
    val readyCashSubmitDetails: LoanNameData? = null,
    @SerialName("loanSummary")
    val loanSummary: UpdateStatus? = null,
    @SerialName("ckycDetails")
    val ckycDetails: UpdateStatus? = null,
    @SerialName("loanAgreement")
    val loanAgreement: UpdateStatus? = null,
    @SerialName("withdrawalDetails")
    val withdrawalDetails: UpdateStatus? = null,
)

@Serializable
data class UpdateStatus(
    @SerialName("status")
    val status: String? = null,
)