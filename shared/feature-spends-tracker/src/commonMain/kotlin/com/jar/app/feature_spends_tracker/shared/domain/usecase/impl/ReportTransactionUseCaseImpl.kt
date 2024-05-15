package com.jar.app.feature_spends_tracker.shared.domain.usecase.impl

import com.jar.app.feature_spends_tracker.shared.data.repository.SpendsTrackerRepository
import com.jar.app.feature_spends_tracker.shared.domain.model.report_transaction.ReportTransactionRequest
import com.jar.app.feature_spends_tracker.shared.domain.usecase.ReportTransactionUseCase

internal class ReportTransactionUseCaseImpl  constructor(
    private val spendsTrackerRepository: SpendsTrackerRepository
) : ReportTransactionUseCase {
    override suspend fun reportTransaction(reportTransactionRequest: ReportTransactionRequest) = spendsTrackerRepository.reportTransaction(reportTransactionRequest)
}