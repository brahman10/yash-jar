package com.jar.app.feature_spends_tracker.shared.domain.repository


import com.jar.app.feature_spends_tracker.shared.data.network.SpendsTrackerDataSource
import com.jar.app.feature_spends_tracker.shared.data.repository.SpendsTrackerRepository
import com.jar.app.feature_spends_tracker.shared.domain.model.report_transaction.ReportTransactionRequest


internal class SpendsTrackerRepositoryImpl  constructor(
    private val spendsTrackerDataSource: SpendsTrackerDataSource
) :
    SpendsTrackerRepository {
    override suspend fun fetchSpendsData() =
        getFlowResult { spendsTrackerDataSource.fetchSpendsData() }

    override suspend fun fetchSpendsTransactionData(page: Int, size: Int) =
        spendsTrackerDataSource.fetchSpendsTransactionData(page = page, size = size)


    override suspend fun fetchSpendsEducationData() =
        getFlowResult {
            spendsTrackerDataSource.fetchSpendsEducationData()
        }

    override suspend fun reportTransaction(reportTransactionRequest: ReportTransactionRequest) =
        getFlowResult {
            spendsTrackerDataSource.reportTransaction(reportTransactionRequest)
        }


}