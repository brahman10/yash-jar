package com.jar.app.feature_spends_tracker.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_spends_tracker.shared.domain.model.report_transaction.ReportTransactionRequest
import kotlinx.coroutines.flow.Flow

interface ReportTransactionUseCase {
    suspend fun reportTransaction(reportTransactionRequest: ReportTransactionRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}