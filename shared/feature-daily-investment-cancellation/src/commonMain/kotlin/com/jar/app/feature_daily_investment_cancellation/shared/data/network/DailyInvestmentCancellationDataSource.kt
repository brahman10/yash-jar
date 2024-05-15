package com.jar.app.feature_daily_investment_cancellation.shared.data.network

import com.jar.app.feature_daily_investment_cancellation.shared.util.DailyInvestmentCancellationConstants
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentConfirmActionDetails
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPauseDetails
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPostCancellationData
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentSettingsData
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

internal class DailyInvestmentCancellationDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchDailyInvestmentSettingsData() =
        getResult<ApiResponseWrapper<DailyInvestmentSettingsData?>> {
            client.get {
                url(DailyInvestmentCancellationConstants.Endpoints.FETCH_DAILY_SAVINGS_SETTING_DATA)
            }
        }

    suspend fun fetchDailyInvestmentPauseDetails() =
        getResult<ApiResponseWrapper<DailyInvestmentPauseDetails>> {
            client.get {
                url(DailyInvestmentCancellationConstants.Endpoints.FETCH_DAILY_SAVINGS_PAUSE_DETAILS)
            }
        }

    suspend fun fetchDailyInvestmentConfirmActionDetails(type: String) =
        getResult<ApiResponseWrapper<DailyInvestmentConfirmActionDetails>> {
        client.get {
            url(DailyInvestmentCancellationConstants.Endpoints.FETCH_DAILY_SAVINGS_CONFIRMATION_DETAILS)
            parameter("type",type)
        }
    }

    suspend fun fetchDailyInvestmentPostCancellation() =
        getResult<ApiResponseWrapper<DailyInvestmentPostCancellationData>> {
            client.get {
                url(DailyInvestmentCancellationConstants.Endpoints.FETCH_DAILY_SAVINGS_POST_CANCELLATION)
            }
        }

}