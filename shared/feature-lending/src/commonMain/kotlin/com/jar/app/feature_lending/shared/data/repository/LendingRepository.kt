package com.jar.app.feature_lending.shared.data.repository

import com.jar.app.feature_lending.shared.domain.model.*
import com.jar.app.feature_lending.shared.domain.model.camps_flow.CamsSdkRedirectionData
import com.jar.app.feature_lending.shared.domain.model.camps_flow.RealtimeBankData
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditDetailedReportResponse
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditSummaryDataResponse
import com.jar.app.feature_lending.shared.domain.model.creditReport.RefreshCreditSummaryDataResponse
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashJourney
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.*
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.BankStatementResponse
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.CreditDetailsResponse
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.RealTimeLeadStatus
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.SuccessApiResponse
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.UpdatePasswordRequest
import com.jar.app.feature_lending.shared.domain.model.repayment.EmiTxnCommonData
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentDetailResponse
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentTransactionDetails
import com.jar.app.feature_lending.shared.domain.model.temp.*
import com.jar.app.feature_lending.shared.domain.model.v2.*
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface LendingRepository : BaseRepository {

    suspend fun fetchLoanApplications(): Flow<RestClientResult<ApiResponseWrapper<LoanApplications?>>>

    suspend fun updateAddressDetails(lendingAddress: LendingAddress): Flow<RestClientResult<ApiResponseWrapper<LendingAddress?>>>

    suspend fun fetchLendingFaq(contentType: String): Flow<RestClientResult<ApiResponseWrapper<LendingFaq?>>>

    suspend fun validateIfscCode(code: String): Flow<RestClientResult<ApiResponseWrapper<BankIfscResponseV2?>>>

    suspend fun validateBankAccount(loanId: String, bankDetails: BankAccountDetails): Flow<RestClientResult<ApiResponseWrapper<BankDetailsResponse?>>>

    suspend fun updateDrawdown(loanId: String, drawdownRequest: DrawdownRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchLendingAgreement(loanId: String): Flow<RestClientResult<ApiResponseWrapper<LendingAgreementResponse?>>>

    suspend fun requestLendingOtp(loanId: String, type: String): Flow<RestClientResult<ApiResponseWrapper<RequestOtpResponseV2?>>>

    suspend fun verifyLendingOtp(data: OtpVerifyRequestData): Flow<RestClientResult<ApiResponseWrapper<ReadyCashVerifyOtpResponse?>>>

    suspend fun fetchMandateLink(loanId: String): Flow<RestClientResult<ApiResponseWrapper<MandateData?>>>

    suspend fun fetchLoanReasonChips(): Flow<RestClientResult<ApiResponseWrapper<LoanReasonChips?>>>

    suspend fun fetchPreApprovedData(): Flow<RestClientResult<ApiResponseWrapper<PreApprovedData?>>>

    suspend fun fetchEmiPlans(amount: Float): Flow<RestClientResult<ApiResponseWrapper<CreditLineSchemeResponse?>>>

    suspend fun fetchLoanApplicationList(): Flow<RestClientResult<ApiResponseWrapper<List<LoanApplicationItemV2>?>>>

    suspend fun updateLoanDetails(updateLoanDetailsBody: UpdateLoanDetailsBodyV2, checkPoint: String): Flow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>

    suspend fun fetchLendingStaticContent(loanId: String?, staticContentType: String): Flow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>
    suspend fun fetchReadyCashLandingScreenData(type: String): Flow<RestClientResult<ApiResponseWrapper<LandingScreenContentResponse?>>>

    suspend fun getLoanDetails(
        loanId: String,
        checkPoint: String?
    ): Flow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>

    suspend fun getLoanProgressStatus(loanId: String): Flow<RestClientResult<ApiResponseWrapper<LendingFlowStatusResponse?>>>

    suspend fun initiateForeclosurePayment(
        initiatePaymentRequest: InitiatePaymentRequest
    ): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>

    suspend fun getRepaymentDetails(loanId: String): Flow<RestClientResult<ApiResponseWrapper<RepaymentDetailResponse?>>>

    suspend fun getEmiTxnHistory(loanId: String, txnType: String): Flow<RestClientResult<ApiResponseWrapper<List<EmiTxnCommonData>?>>>

    suspend fun getTransactionDetails(paymentId: String): Flow<RestClientResult<ApiResponseWrapper<RepaymentTransactionDetails?>>>

    suspend fun fetchReadyCashJourney(): Flow<RestClientResult<ApiResponseWrapper<ReadyCashJourney?>>>

    suspend fun updateNotifyUser(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun acknowledgeOneTimeCard(cardType: String): Flow<RestClientResult<ApiResponseWrapper<Boolean?>>>

    suspend fun uploadBankStatement(filename: String, byteArray: ByteArray): Flow<RestClientResult<ApiResponseWrapper<SuccessApiResponse?>>>

    suspend fun getBankStatement(): Flow<RestClientResult<ApiResponseWrapper<BankStatementResponse?>>>

    suspend fun updateBankDetails(bankAccount: BankAccount): Flow<RestClientResult<ApiResponseWrapper<SuccessApiResponse?>>>

    suspend fun updateBankStatementPassword(updatePasswordRequest: UpdatePasswordRequest): Flow<RestClientResult<ApiResponseWrapper<SuccessApiResponse?>>>

    suspend fun getRealTimeCreditDetails(): Flow<RestClientResult<ApiResponseWrapper<CreditDetailsResponse?>>>

    suspend fun getRealTimeLeadStatus(): Flow<RestClientResult<ApiResponseWrapper<RealTimeLeadStatus?>>>

    suspend fun fetchCamsBanks(): Flow<RestClientResult<ApiResponseWrapper<List<RealtimeBankData>?>>>

    suspend fun fetchCamsSdkRedirectData(fipId: String): Flow<RestClientResult<ApiResponseWrapper<CamsSdkRedirectionData?>>>

    suspend fun fetchCamsDataStatus(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun scheduleBankUptimeNotification(fipId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchPANStatus(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchExperianReport(fetchExperianReportRequest: FetchExperianReportRequest): Flow<RestClientResult<ApiResponseWrapper<SuccessApiResponse?>>>

    suspend fun fetchCreditReportSummary(): Flow<RestClientResult<ApiResponseWrapper<CreditSummaryDataResponse?>>>

    suspend fun fetchCreditDetailedReportData(type: String): Flow<RestClientResult<ApiResponseWrapper<CreditDetailedReportResponse?>>>

    suspend fun refreshCreditReportSummary():  Flow<RestClientResult<ApiResponseWrapper<RefreshCreditSummaryDataResponse?>>>

}