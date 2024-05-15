package com.jar.app.feature_vasooli.impl.domain.repository

import com.jar.app.feature_vasooli.impl.data.network.VasooliDataSource
import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.model.*

internal class VasooliRepositoryImpl constructor(
    private val vasooliDataSource: VasooliDataSource
): VasooliRepository {

    override suspend fun fetchVasooliOverview() = getFlowResult {
        vasooliDataSource.fetchVasooliOverview()
    }

    override suspend fun fetchLoansList() = getFlowResult {
        vasooliDataSource.fetchLoansList()
    }

    override suspend fun postVasooliRequest(vasooliEntryRequest: VasooliEntryRequest) = getFlowResult {
        vasooliDataSource.postVasooliRequest(vasooliEntryRequest)
    }

    override suspend fun fetchRepaymentHistory(loadId: String) = getFlowResult {
        vasooliDataSource.fetchRepaymentHistory(loadId)
    }

    override suspend fun postRepaymentEntryRequest(repaymentEntryRequest: RepaymentEntryRequest) = getFlowResult {
        vasooliDataSource.postRepaymentEntryRequest(repaymentEntryRequest)
    }

    override suspend fun updateVasooliStatus(updateStatusRequest: UpdateStatusRequest) = getFlowResult {
        vasooliDataSource.updateVasooliStatus(updateStatusRequest)
    }

    override suspend fun deleteVasooliEntry(loanId: String) = getFlowResult {
        vasooliDataSource.deleteVasooliEntry(loanId)
    }

    override suspend fun fetchLoanDetails(loanId: String) = getFlowResult {
        vasooliDataSource.fetchLoanDetails(loanId)
    }

    override suspend fun updateVasooliEntry(updateEntryRequest: UpdateEntryRequest) = getFlowResult {
        vasooliDataSource.updateVasooliEntry(updateEntryRequest)
    }

    override suspend fun fetchReminder(loanId: String, medium: String) = getFlowResult {
        vasooliDataSource.fetchReminder(loanId, medium)
    }

    override suspend fun fetchNewImage(ignoreIndex: String) = getFlowResult {
        vasooliDataSource.fetchNewImage(ignoreIndex)
    }

    override suspend fun sendReminder(sendReminderRequest: SendReminderRequest) = getFlowResult {
        vasooliDataSource.sendReminder(sendReminderRequest)
    }
}