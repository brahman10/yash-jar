package com.jar.app.feature_lending.shared.data.network

import com.jar.app.feature_lending.shared.domain.model.BankDetailsResponse
import com.jar.app.feature_lending.shared.domain.model.LendingFaq
import com.jar.app.feature_lending.shared.domain.model.LendingFlowStatusResponse
import com.jar.app.feature_lending.shared.domain.model.LoanReasonChips
import com.jar.app.feature_lending.shared.domain.model.OtpVerifyRequestData
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.BankStatementResponse
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.CreditDetailsResponse
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.FetchExperianReportRequest
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.RealTimeLeadStatus
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.SuccessApiResponse
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.UpdatePasswordRequest
import com.jar.app.feature_lending.shared.domain.model.repayment.EmiTxnCommonData
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentDetailResponse
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentTransactionDetails
import com.jar.app.feature_lending.shared.domain.model.temp.BankAccountDetails
import com.jar.app.feature_lending.shared.domain.model.temp.DrawdownRequest
import com.jar.app.feature_lending.shared.domain.model.temp.LendingAddress
import com.jar.app.feature_lending.shared.domain.model.temp.LendingAgreementResponse
import com.jar.app.feature_lending.shared.domain.model.temp.LoanApplications
import com.jar.app.feature_lending.shared.domain.model.temp.MandateData
import com.jar.app.feature_lending.shared.domain.model.v2.BankAccount
import com.jar.app.feature_lending.shared.domain.model.v2.BankIfscResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.CreditLineSchemeResponse
import com.jar.app.feature_lending.shared.domain.model.v2.InitiatePaymentRequest
import com.jar.app.feature_lending.shared.domain.model.v2.LandingScreenContentResponse
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationItemV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import com.jar.app.feature_lending.shared.domain.model.v2.PreApprovedData
import com.jar.app.feature_lending.shared.domain.model.v2.ReadyCashVerifyOtpResponse
import com.jar.app.feature_lending.shared.domain.model.v2.RequestOtpResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.StaticContentResponse
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.model.camps_flow.CamsSdkRedirectionData
import com.jar.app.feature_lending.shared.domain.model.camps_flow.RealtimeBankData
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditDetailedReportResponse
import com.jar.app.feature_lending.shared.domain.model.creditReport.CreditSummaryDataResponse
import com.jar.app.feature_lending.shared.domain.model.creditReport.RefreshCreditSummaryDataResponse
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashJourney
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.app.feature_lending.shared.util.LendingConstants.Endpoints
import com.jar.app.feature_lending.shared.util.LendingConstants.LendingApplicationCheckpoints
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

internal class LendingDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {


    suspend fun updateAddressDetails(lendingAddress: LendingAddress) =
        getResult<ApiResponseWrapper<LendingAddress?>> {
            client.post {
                url(Endpoints.UPDATE_ADDRESS_DETAIL)
                parameter("checkpoint", LendingApplicationCheckpoints.ADDRESS)
                setBody(lendingAddress)
            }
        }

    suspend fun fetchLendingFaq(contentType: String) = getResult<ApiResponseWrapper<LendingFaq?>> {
        client.get {
            url(Endpoints.FETCH_LENDING_FAQ)
            parameter("contentType", contentType)
        }
    }

    suspend fun validateIfscCode(code: String) =
        getResult<ApiResponseWrapper<BankIfscResponseV2?>> {
            client.get {
                url(Endpoints.VALIDATE_IFSC_CODE)
                parameter("ifsc", code)
            }
        }

    suspend fun validateBankAccount(loanId: String, bankDetails: BankAccountDetails) =
        getResult<ApiResponseWrapper<BankDetailsResponse?>> {
            client.post {
                url(Endpoints.VALIDATE_BANK_ACCOUNT)
                parameter("id", loanId)
                setBody(bankDetails)
            }
        }

    suspend fun updateDrawdown(loanId: String, drawdownRequest: DrawdownRequest) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.UPDATE_DRAW_DOWN)
                parameter("id", loanId)
                setBody(drawdownRequest)
            }
        }

    suspend fun fetchLendingAgreement(loanId: String) =
        getResult<ApiResponseWrapper<LendingAgreementResponse?>> {
            client.get {
                url(Endpoints.FETCH_LOAN_AGREEMENT)
                parameter("id", loanId)
            }
        }

    suspend fun requestLendingOtp(loanId: String, type: String) =
        getResult<ApiResponseWrapper<RequestOtpResponseV2?>> {
            client.get {
                url(Endpoints.REQUEST_LENDING_OTP)
                parameter("applicationId", loanId)
                parameter("type", type)
            }
        }

    suspend fun verifyLendingOtp(
        data: OtpVerifyRequestData
    ) =
        getResult<ApiResponseWrapper<ReadyCashVerifyOtpResponse?>> {
            client.post {
                url(Endpoints.VERIFY_LENDING_OTP)
                setBody(data)
            }
        }

    suspend fun fetchMandateLink(loanId: String) = getResult<ApiResponseWrapper<MandateData?>> {
        client.get {
            url(Endpoints.FETCH_MANDATE_LINK)
            parameter("id", loanId)
        }
    }

    suspend fun fetchLoanApplications() =
        getResult<ApiResponseWrapper<LoanApplications?>> {
            client.get {
                url(Endpoints.FETCH_LOAN_APPLICATIONS)
            }
        }

    suspend fun fetchLoanReasonChips() = getResult<ApiResponseWrapper<LoanReasonChips?>> {
        client.get {
            url(Endpoints.FETCH_LOAN_REASONS)
            parameter("contentType", LendingConstants.StaticContentType.LOAN_NAME_CHIPS)
        }
    }

    suspend fun fetchPreApprovedData() =
        getResult<ApiResponseWrapper<PreApprovedData?>> {
            client.get {
                url(Endpoints.FETCH_PRE_APPROVED_DATA)
            }
        }

    suspend fun fetchEmiPlans(amount: Float) =
        getResult<ApiResponseWrapper<CreditLineSchemeResponse?>> {
            client.get {
                url(Endpoints.FETCH_EMI_PLANS)
                parameter("amount", amount)
            }
        }

    suspend fun fetchLoanApplicationList() =
        getResult<ApiResponseWrapper<List<LoanApplicationItemV2>?>> {
            client.get {
                url(Endpoints.FETCH_LOAN_APPLICATION_LIST)
            }
        }

    suspend fun updateLoanDetails(
        updateLoanDetailsBody: UpdateLoanDetailsBodyV2,
        checkpoint: String
    ) = getResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>> {
        client.post {
            url(Endpoints.UPDATE_LOAN_DETAILS)
            setBody(updateLoanDetailsBody)
            parameter("checkpoint", checkpoint)
        }
    }

    suspend fun fetchLendingStaticContent(applicationId: String?, staticContentType: String) =
        getResult<ApiResponseWrapper<StaticContentResponse?>> {
            client.get {
                url(Endpoints.FETCH_LENDING_STATIC_CONTENT)
                applicationId?.let {
                    parameter("applicationId", it)
                }
                parameter("type", staticContentType)
            }
        }

    suspend fun getLoanDetails(applicationId: String, checkpoint: String?) =
        getResult<ApiResponseWrapper<LoanDetailsV2?>> {
            client.get {
                url(Endpoints.FETCH_LOAN_DETAILS)
                parameter("applicationId", applicationId)
                parameter("checkpoint", checkpoint)
            }
        }

    suspend fun getLoanProgressStatus(applicationId: String) =
        getResult<ApiResponseWrapper<LendingFlowStatusResponse?>> {
            client.get {
                url(Endpoints.FETCH_LOAN_APPLICATION_STATUS)
                parameter("applicationId", applicationId)
            }
        }

    suspend fun initiateForeclosurePayment(initiatePaymentRequest: InitiatePaymentRequest) =
        getResult<ApiResponseWrapper<InitiatePaymentResponse?>> {
            client.get {
                url(Endpoints.INITIATE_FORECLOSURE_PAYMENT)
                parameter("txnAmt", initiatePaymentRequest.txnAmt)
                parameter("orderId", initiatePaymentRequest.orderId)
                parameter("paymentProvider", initiatePaymentRequest.paymentProvider)
                parameter("loantxnCategory", initiatePaymentRequest.loanTxnCategory)
                parameter("transactionType", initiatePaymentRequest.transactionType)
                parameter("emiType", initiatePaymentRequest.emiType)
            }
        }

    suspend fun getRepaymentDetails(applicationId: String) =
        getResult<ApiResponseWrapper<RepaymentDetailResponse?>> {
            client.get {
                url(Endpoints.FETCH_REPAYMENT_DETAILS)
                parameter("applicationId", applicationId)
            }
        }

    suspend fun fetchReadyCashLandingScreenData(type: String) =
        getResult<ApiResponseWrapper<LandingScreenContentResponse?>> {
            client.get {
                url(Endpoints.FETCH_READYCASH_LANDING_SCREEN_DATA)
                parameter("type", type)
            }
        }

    suspend fun getEmiTxnHistory(loanId: String, txnType: String) =
        getResult<ApiResponseWrapper<List<EmiTxnCommonData>?>> {
            client.get {
                url(Endpoints.FETCH_EMI_TRANSACTION_HISTORY)
                parameter("applicationId", loanId)
                parameter("txnType", txnType)
            }
        }

    suspend fun getTransactionDetails(paymentId: String) =
        getResult<ApiResponseWrapper<RepaymentTransactionDetails?>> {
            client.get {
                url(Endpoints.FETCH_TRANSACTION_DETAILS)
                parameter("paymentId", paymentId)
            }
        }

    suspend fun fetchReadyCashJourney() =
        getResult<ApiResponseWrapper<ReadyCashJourney?>> {
            client.get {
                url(Endpoints.FETCH_READY_CASH_JOURNEY)
            }
        }

    suspend fun updateNotifyUser() =
        getResult<ApiResponseWrapper<Unit?>> {
            client.get {
                url(Endpoints.SCHEDULE_USER_NOTIFICATION)
            }
        }

    suspend fun acknowledgeOneTimeCard(cardType: String) =
        getResult<ApiResponseWrapper<Boolean?>> {
            client.post {
                url(Endpoints.ACKNOWLEDGE_ONE_TIME_CARD)
                parameter("screen", cardType)
            }
        }


    suspend fun postBankStatement(fileName: String, byteArray: ByteArray) =
        getResult<ApiResponseWrapper<SuccessApiResponse?>> {
            client.post {
                url(Endpoints.UPLOAD_BANK_STATEMENT)
                setBody(MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            byteArray,
                            Headers.build {
                                append(
                                    HttpHeaders.ContentType,
                                    "application/pdf"
                                )
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=$fileName"
                                )
                                append(HttpHeaders.ContentLength, "${byteArray.size}")
                            }
                        )

                    }
                ))
            }
        }

    suspend fun getBankStatement() =
        getResult<ApiResponseWrapper<BankStatementResponse?>> {
            client.get {
                url(Endpoints.GET_UPLOADED_BANK_STATEMENTS)
            }
        }

    suspend fun updateBankDetails(
        bankAccount: BankAccount
    ) = getResult<ApiResponseWrapper<SuccessApiResponse?>> {
        client.post {
            url(Endpoints.UPDATE_BANK_DETAIL)
            setBody(bankAccount)
        }
    }

    suspend fun updateBankStatementPassword(
        updatePasswordRequest: UpdatePasswordRequest
    ) = getResult<ApiResponseWrapper<SuccessApiResponse?>> {
        client.post {
            url(Endpoints.SET_BANK_STATEMENT_PASSWORD)
            setBody(updatePasswordRequest)
        }
    }

    suspend fun getRealTimeCreditDetails(
    ) = getResult<ApiResponseWrapper<CreditDetailsResponse?>> {
        client.get {
            url(Endpoints.REAL_TIME_CREDIT_DETAILS)
        }
    }

    suspend fun getRealTimeLeadStatus(
    ) = getResult<ApiResponseWrapper<RealTimeLeadStatus?>> {
        client.get {
            url(Endpoints.REAL_TIME_LEAD_STATUS)
        }
    }

    suspend fun fetchCamsBanks() =
        getResult<ApiResponseWrapper<List<RealtimeBankData>?>> {
            client.get {
                url(Endpoints.FETCH_CAMS_BANKS)
            }
        }

    suspend fun fetchCamsSdkRedirectData(fipId: String) =
        getResult<ApiResponseWrapper<CamsSdkRedirectionData?>> {
            client.get {
                url(Endpoints.FETCH_CAMS_SDK_REDIRECT_DATA)
                parameter("fipId", fipId)
            }
        }

    suspend fun fetchCamsDataStatus() =
        getResult<ApiResponseWrapper<Unit?>> {
            client.get {
                url(Endpoints.FETCH_CAMS_DATA_STATUS)
            }
        }

    suspend fun updateCamsDowntime(fipId: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.UPDATE_CAMS_DOWNTIME)
                parameter("fipId", fipId)
            }
        }

    suspend fun fetchPANStatus() =
        getResult<ApiResponseWrapper<Unit?>> {
            client.get {
                url(Endpoints.FETCH_PAN_STATUS)
            }
        }

    suspend fun fetchExperianReport(fetchExperianReportRequest: FetchExperianReportRequest) =
        getResult<ApiResponseWrapper<SuccessApiResponse?>> {
            client.post {
                url(Endpoints.FETCH_EXPERIAN_REPORT)
                setBody(fetchExperianReportRequest)
            }
        }

    suspend fun fetchCreditReportSummary(
    ) = getResult<ApiResponseWrapper<CreditSummaryDataResponse?>> {
        client.get {
            url(Endpoints.CREDIT_REPORT_SUMMARY)
        }
    }

    suspend fun fetchCreditDetailedReportData(type: String) =
        getResult<ApiResponseWrapper<CreditDetailedReportResponse?>> {
            client.get {
                url(Endpoints.CREDIT_DETAILED_REPORT)
                parameter("type", type)
            }
        }

    suspend fun refreshCreditReportSummary(
    ) = getResult<ApiResponseWrapper<RefreshCreditSummaryDataResponse?>> {
        client.get {
            url(Endpoints.CREDIT_REPORT_SUMMARY_REFRESH)
        }
    }
}
