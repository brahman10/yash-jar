package com.jar.app.feature_lending.shared.domain.model.temp

import com.jar.app.core_base.domain.model.KycProgressResponse
import com.jar.app.feature_lending.shared.domain.model.LoanDetail
import com.jar.app.feature_user_api.domain.model.Address
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LoanApplications(
    @SerialName("applications")
    val applications: List<LoanApplicationItem>,

    @SerialName("kycJourney")
    val kycJourney: KycProgressResponse?
) {
    fun getDescriptionAccordingToStatus(): StringResource {
        val loanApplicationDetail = applications.getOrNull(0)?.details
        return if (loanApplicationDetail?.LEAD_CREATION == null || loanApplicationDetail.LEAD_CREATION.status.orEmpty() != LoanStatus.VERIFIED.name) {
            com.jar.app.feature_lending.shared.MR.strings.feature_lending_check_your_readycash_eligiblity
        } else if (loanApplicationDetail.MANDATE_SETUP == null || loanApplicationDetail.MANDATE_SETUP.status != MandateStatus.VERIFIED.name) {
            com.jar.app.feature_lending.shared.MR.strings.feature_lending_automate_emi_description
        } else {
            com.jar.app.feature_lending.shared.MR.strings.feature_lending_application_step_description
        }
    }
}

@kotlinx.serialization.Serializable
data class LoanApplicationItem(
    @SerialName("applicationId")
    val applicationId: String,

    @SerialName("details")
    val details: LoanApplicationDetail
)

@Parcelize
@kotlinx.serialization.Serializable
data class LoanApplicationDetail(
    var loanId: String,
    @SerialName("LOAN_AGREEMENT")
    val LOAN_AGREEMENT: LendingAgreement? = null,

    @SerialName("DRAW_DOWN")
    val DRAW_DOWN: DrawdownRequest? = null,

    @SerialName("EMPLOYMENT_DETAILS")
    val EMPLOYMENT_DETAILS: EmploymentDetailsRequest? = null,

    @SerialName("ADDRESS")
    val ADDRESS: Address? = null,

    @SerialName("BANK_ACCOUNT_DETAILS")
    val BANK_ACCOUNT_DETAILS: BankAccountDetails? = null,

    @SerialName("LEAD_CREATION")
    val LEAD_CREATION: LoanLeadCreation? = null,

    @SerialName("MANDATE_SETUP")
    val MANDATE_SETUP: MandateData? = null,

    @SerialName("LOAN_DETAILS")
    val LOAN_DETAILS: LoanDetail? = null,

    @SerialName("disbursedLoanInfo")
    val disbursedLoanInfo: DisbursedLoanInfo? = null
) : Parcelable

enum class LoanStatus {
    PENDING,//not yet visited
    IN_PROGRESS,
    VERIFIED,
    CALLBACK_PENDING,//special case for bank penny drop failed to get callback after 90 seconds.
    FAILED //FOR UI
}