package com.jar.app.feature_post_setup.data.repository

import com.jar.app.core_base.domain.model.GenericFaqList
import com.jar.app.feature_post_setup.domain.model.calendar.CalendarDataResp
import com.jar.app.feature_post_setup.domain.model.UserPostSetupData
import com.jar.app.feature_post_setup.domain.model.setting.PostSetupQuickActionList
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_post_setup.domain.model.DSFailureInfo
import com.jar.app.feature_post_setup.domain.model.calendar.CalendarSavingOperations
import kotlinx.coroutines.flow.Flow

interface PostSetupRepository : BaseRepository {

    suspend fun fetchPostSetupUserData(): Flow<RestClientResult<ApiResponseWrapper<UserPostSetupData>>>

    suspend fun fetchPostSetupCalendarData(
        startDate: String,
        endDate: String
    ): Flow<RestClientResult<ApiResponseWrapper<CalendarDataResp>>>

    suspend fun fetchPostSetupQuickActions(): Flow<RestClientResult<ApiResponseWrapper<PostSetupQuickActionList>>>

    suspend fun fetchPostSetupSavingOperations(): Flow<RestClientResult<ApiResponseWrapper<CalendarSavingOperations>>>

    suspend fun fetchPostSetupFaq(): Flow<RestClientResult<ApiResponseWrapper<GenericFaqList>>>
    suspend fun fetchPostSetupFailureInfo(): Flow<RestClientResult<ApiResponseWrapper<DSFailureInfo?>>>

    suspend fun initiatePaymentForFailedTransactions(
        amount: Float,
        paymentProvider: String,
        type: String,
        roundOffsLinked: List<String>
    ): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse>>>
}