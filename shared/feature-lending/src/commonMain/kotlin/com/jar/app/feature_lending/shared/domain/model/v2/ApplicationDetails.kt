package com.jar.app.feature_lending.shared.domain.model.v2

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ApplicationDetails(
    @SerialName("aadhaar")
    val aadhaar: Aadhaar? = null,
    @SerialName("bankAccount")
    val bankAccount: BankAccount? = null,
    @SerialName("drawdown")
    val drawdown: Drawdown? = null,
    @SerialName("email")
    val email: Email? = null,
    @SerialName("employment")
    val employment: Employment? = null,
    @SerialName("pan")
    val pan: Pan? = null,
    @SerialName("selfie")
    val selfie: Selfie? = null,
    @SerialName("loanAgreement")
    val loanAgreement: AgreementDataV2? = null,
    @SerialName("ckycDetails")
    val ckycDetails: CkycDetail? = null,
    @SerialName("loanSummary")
    val loanSummary: LoanSummaryV2? = null,
    @SerialName("mandateSetup")
    val mandateDetails: MandateDetailsV2? = null,
    @SerialName("withdrawal")
    val withdrawal: Withdrawal? = null,
    @SerialName("foreclosure")
    val foreclosure: ForeclosureData? = null,
)