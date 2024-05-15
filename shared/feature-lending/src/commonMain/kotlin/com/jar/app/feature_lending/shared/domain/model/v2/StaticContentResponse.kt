package com.jar.app.feature_lending.shared.domain.model.v2


import com.jar.app.feature_lending.shared.domain.model.ReasonData
import com.jar.app.feature_lending.shared.domain.model.mandate.MandateSetupFailureContent
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditReport
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.RealTimeBankDetailSteps
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.RealTimeLanding
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class StaticContentResponse(
    @SerialName("aadhaarContent")
    val aadhaarContent: AadhaarContent? = null,
    @SerialName("bankContent")
    val bankContent: List<String>? = null,
    @SerialName("experianLogo")
    val experianLogo: String? = null,
    @SerialName("feesAndChargesDescription")
    val feesAndChargesDescription: List<QuestionAnswer>? = null,
    @SerialName("kycConfirmationConsent")
    val kycConfirmationConsent: String? = null,
    @SerialName("lenderLogo")
    val lenderLogo: String? = null,
    @SerialName("loanAgreement")
    val loanAgreement: LoanAgreement? = null,
    @SerialName("npciLogo")
    val npciLogo: String? = null,
    @SerialName("readyCashBreakDownDescription")
    val readyCashBreakDownDescription: List<QuestionAnswer>? = null,
    @SerialName("applicationRejectedData")
    val applicationRejectedData: ApplicationRejectionData? = null,
    @SerialName("loanNameChips")
    val loanNameChips: List<ReasonData>? = null,
    @SerialName("mandateSetupContent")
    val mandateSetupContent: MandateConsentData? = null,
    @SerialName("lenderEligibilityRange")
    val lenderEligibilityRange: LendingEligibilityRange? = null,
    @SerialName("repaymentTransactionContent")
    val repaymentTransactionContent: String? = null,
    @SerialName("repaymentEmiContent")
    val repaymentEmiContent: String? = null,
    @SerialName("downTime")
    val downTime: PartnerDownTimeData? = null,
    @SerialName("realTime")
    val realTime: RealTimeLanding? = null,
    @SerialName("realTimeBankDetails")
    val realTimeBankDetailSteps: RealTimeBankDetailSteps? = null,
    @SerialName("mandateSetupFailureContent")
    val mandateSetupFailureContent: MandateSetupFailureContent? = null,
    @SerialName("mandateSetupUpdatedContent")
    val mandateSetupUpdatedContent: MandateSetupUpdatedContent? = null,
    @SerialName("creditReport")
    val creditReport: CreditReport? = null
)