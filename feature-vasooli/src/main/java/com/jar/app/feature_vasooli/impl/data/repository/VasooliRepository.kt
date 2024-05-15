package com.jar.app.feature_vasooli.impl.data.repository

import com.jar.app.feature_vasooli.impl.domain.model.*
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface VasooliRepository : BaseRepository {

    suspend fun fetchVasooliOverview(): Flow<RestClientResult<ApiResponseWrapper<VasooliOverview>>>

    suspend fun fetchLoansList(): Flow<RestClientResult<ApiResponseWrapper<List<Borrower>>>>

    suspend fun postVasooliRequest(vasooliEntryRequest: VasooliEntryRequest): Flow<RestClientResult<ApiResponseWrapper<VasooliEntryResponse>>>

    suspend fun fetchRepaymentHistory(loadId: String): Flow<RestClientResult<ApiResponseWrapper<List<Repayment>>>>

    suspend fun postRepaymentEntryRequest(repaymentEntryRequest: RepaymentEntryRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun updateVasooliStatus(updateStatusRequest: UpdateStatusRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun deleteVasooliEntry(loanId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchLoanDetails(loanId: String): Flow<RestClientResult<ApiResponseWrapper<Borrower>>>

    suspend fun updateVasooliEntry(updateEntryRequest: UpdateEntryRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchReminder(loanId: String, medium: String): Flow<RestClientResult<ApiResponseWrapper<Reminder>>>

    suspend fun fetchNewImage(ignoreIndex: String): Flow<RestClientResult<ApiResponseWrapper<Reminder>>>

    suspend fun sendReminder(sendReminderRequest: SendReminderRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}