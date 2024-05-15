package com.jar.app.feature_lending.shared.domain.repository

import com.jar.app.feature_lending.shared.data.network.LendingDataSource
import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.model.OtpVerifyRequestData
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.FetchExperianReportRequest
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.UpdatePasswordRequest
import com.jar.app.feature_lending.shared.domain.model.temp.BankAccountDetails
import com.jar.app.feature_lending.shared.domain.model.temp.DrawdownRequest
import com.jar.app.feature_lending.shared.domain.model.temp.LendingAddress
import com.jar.app.feature_lending.shared.domain.model.v2.BankAccount
import com.jar.app.feature_lending.shared.domain.model.v2.InitiatePaymentRequest
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2

internal class LendingRepositoryImpl constructor(
    private val lendingDataSource: LendingDataSource
) : LendingRepository {

    override suspend fun updateAddressDetails(lendingAddress: LendingAddress) = getFlowResult {
        lendingDataSource.updateAddressDetails(lendingAddress)
    }


    override suspend fun fetchLendingFaq(contentType: String) = getFlowResult {
        lendingDataSource.fetchLendingFaq(contentType)
    }

    override suspend fun validateIfscCode(code: String) = getFlowResult {
        lendingDataSource.validateIfscCode(code)
    }

    override suspend fun validateBankAccount(loanId: String, bankDetails: BankAccountDetails) =
        getFlowResult {
            lendingDataSource.validateBankAccount(loanId, bankDetails)
        }

    override suspend fun updateDrawdown(loanId: String, drawdownRequest: DrawdownRequest) =
        getFlowResult {
            lendingDataSource.updateDrawdown(loanId, drawdownRequest)
        }

    override suspend fun fetchLendingAgreement(loanId: String) = getFlowResult {
        lendingDataSource.fetchLendingAgreement(loanId)
    }

    override suspend fun requestLendingOtp(loanId: String, type: String) = getFlowResult {
        lendingDataSource.requestLendingOtp(loanId, type)
    }

    override suspend fun verifyLendingOtp(data: OtpVerifyRequestData) = getFlowResult {
        lendingDataSource.verifyLendingOtp(data)
    }

    override suspend fun fetchMandateLink(loanId: String) = getFlowResult {
        lendingDataSource.fetchMandateLink(loanId)
    }

    override suspend fun fetchLoanReasonChips() = getFlowResult {
        lendingDataSource.fetchLoanReasonChips()
    }

    override suspend fun fetchLoanApplications() = getFlowResult {
        lendingDataSource.fetchLoanApplications()
    }

    override suspend fun fetchPreApprovedData() = getFlowResult {
        lendingDataSource.fetchPreApprovedData()
    }

    override suspend fun fetchEmiPlans(amount: Float) = getFlowResult {
        lendingDataSource.fetchEmiPlans(amount)
    }

    override suspend fun fetchLoanApplicationList() = getFlowResult {
        lendingDataSource.fetchLoanApplicationList()
    }

    override suspend fun updateLoanDetails(
        updateLoanDetailsBody: UpdateLoanDetailsBodyV2,
        checkPoint: String
    ) = getFlowResult {
        lendingDataSource.updateLoanDetails(updateLoanDetailsBody, checkPoint)
    }

    override suspend fun fetchLendingStaticContent(loanId: String?, staticContentType: String) =
        getFlowResult {
            lendingDataSource.fetchLendingStaticContent(loanId, staticContentType)
        }

    override suspend fun getLoanDetails(loanId: String, checkPoint: String?) = getFlowResult {
        lendingDataSource.getLoanDetails(loanId, checkPoint)
    }

    override suspend fun fetchReadyCashLandingScreenData(type: String) = getFlowResult {
        lendingDataSource.fetchReadyCashLandingScreenData(type)
    }

    override suspend fun getLoanProgressStatus(loanId: String) = getFlowResult {
        lendingDataSource.getLoanProgressStatus(loanId)
    }

    override suspend fun initiateForeclosurePayment(initiatePaymentRequest: InitiatePaymentRequest) =
        getFlowResult {
            lendingDataSource.initiateForeclosurePayment(initiatePaymentRequest)
        }

    override suspend fun getRepaymentDetails(loanId: String) = getFlowResult {
        lendingDataSource.getRepaymentDetails(loanId)
    }

    override suspend fun getEmiTxnHistory(loanId: String, txnType: String) = getFlowResult {
        lendingDataSource.getEmiTxnHistory(loanId, txnType)
    }

    override suspend fun getTransactionDetails(paymentId: String) = getFlowResult {
        lendingDataSource.getTransactionDetails(paymentId)
    }

    override suspend fun fetchReadyCashJourney() = getFlowResult {
        lendingDataSource.fetchReadyCashJourney()
    }

    override suspend fun updateNotifyUser() = getFlowResult {
        lendingDataSource.updateNotifyUser()
    }

    override suspend fun acknowledgeOneTimeCard(cardType: String) = getFlowResult {
        lendingDataSource.acknowledgeOneTimeCard(cardType)
    }

    override suspend fun uploadBankStatement(filename: String, byteArray: ByteArray) =
        getFlowResult {
            lendingDataSource.postBankStatement(filename, byteArray)
        }

    override suspend fun getBankStatement() = getFlowResult {
        lendingDataSource.getBankStatement()
    }

    override suspend fun updateBankDetails(bankAccount: BankAccount) = getFlowResult {
        lendingDataSource.updateBankDetails(bankAccount)
    }

    override suspend fun updateBankStatementPassword(updatePasswordRequest: UpdatePasswordRequest) =
        getFlowResult {
            lendingDataSource.updateBankStatementPassword(updatePasswordRequest)
        }

    override suspend fun getRealTimeCreditDetails() = getFlowResult {
        lendingDataSource.getRealTimeCreditDetails()
    }

    override suspend fun getRealTimeLeadStatus() = getFlowResult {
        lendingDataSource.getRealTimeLeadStatus()
    }

    override suspend fun fetchCamsBanks() = getFlowResult {
        lendingDataSource.fetchCamsBanks()
    }

    override suspend fun fetchCamsSdkRedirectData(fipId: String) = getFlowResult {
        lendingDataSource.fetchCamsSdkRedirectData(fipId)
    }

    override suspend fun fetchCamsDataStatus() = getFlowResult {
        lendingDataSource.fetchCamsDataStatus()
    }

    override suspend fun scheduleBankUptimeNotification(fipId: String) = getFlowResult {
        lendingDataSource.updateCamsDowntime(fipId)
    }

    override suspend fun fetchPANStatus() = getFlowResult {
        lendingDataSource.fetchPANStatus()
    }

    override suspend fun fetchExperianReport(fetchExperianReportRequest: FetchExperianReportRequest) =
        getFlowResult {
            lendingDataSource.fetchExperianReport(fetchExperianReportRequest)
        }

    override suspend fun fetchCreditReportSummary() = getFlowResult {
        lendingDataSource.fetchCreditReportSummary()
    }

    override suspend fun fetchCreditDetailedReportData(type: String) = getFlowResult {
        lendingDataSource.fetchCreditDetailedReportData(type)
    }

    override suspend fun refreshCreditReportSummary() = getFlowResult {
        lendingDataSource.refreshCreditReportSummary()
    }
}