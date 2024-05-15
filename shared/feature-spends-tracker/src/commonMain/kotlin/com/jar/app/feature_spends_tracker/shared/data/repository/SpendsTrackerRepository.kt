package com.jar.app.feature_spends_tracker.shared.data.repository

import com.jar.app.feature_spends_tracker.shared.domain.model.report_transaction.ReportTransactionRequest
import com.jar.app.feature_spends_tracker.shared.domain.model.spendsDetailsData.SpendsData
import com.jar.app.feature_spends_tracker.shared.domain.model.spends_education.SpendsEducationData
import com.jar.app.feature_spends_tracker.shared.domain.model.spends_transaction_data.SpendsTransactionData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import kotlinx.coroutines.flow.Flow

interface SpendsTrackerRepository : BaseRepository {

    suspend fun fetchSpendsData(): Flow<RestClientResult<ApiResponseWrapper<SpendsData>>>

    suspend fun fetchSpendsTransactionData(
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<List<SpendsTransactionData>>>

    suspend fun fetchSpendsEducationData(): Flow<RestClientResult<ApiResponseWrapper<SpendsEducationData?>>>

    suspend fun reportTransaction(reportTransactionRequest: ReportTransactionRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

}