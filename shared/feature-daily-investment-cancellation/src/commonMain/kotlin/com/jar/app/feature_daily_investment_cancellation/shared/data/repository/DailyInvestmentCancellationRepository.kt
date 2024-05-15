package com.jar.app.feature_daily_investment_cancellation.shared.data.repository

import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentConfirmActionDetails
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPauseDetails
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPostCancellationData
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentSettingsData
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface DailyInvestmentCancellationRepository : BaseRepository {

    suspend fun fetchDailyInvestmentSettingsData(): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentSettingsData?>>>

    suspend fun fetchDailyInvestmentPauseDetails(): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentPauseDetails>>>

    suspend fun fetchDailyInvestmentConfirmActionDetails(type: String): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentConfirmActionDetails>>>

    suspend fun fetchDailyInvestmentPostCancellation(): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentPostCancellationData>>>

}