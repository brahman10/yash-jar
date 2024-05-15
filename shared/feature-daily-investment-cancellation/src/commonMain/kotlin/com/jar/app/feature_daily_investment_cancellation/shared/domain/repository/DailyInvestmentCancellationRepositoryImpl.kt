package com.jar.app.feature_daily_investment_cancellation.shared.domain.repository

import com.jar.app.feature_daily_investment_cancellation.shared.data.repository.DailyInvestmentCancellationRepository
import com.jar.app.feature_daily_investment_cancellation.shared.data.network.DailyInvestmentCancellationDataSource

internal class DailyInvestmentCancellationRepositoryImpl constructor(
    private val dailyInvestmentCancellationDataSource: DailyInvestmentCancellationDataSource
) : DailyInvestmentCancellationRepository {
    override suspend fun fetchDailyInvestmentSettingsData() = getFlowResult {
        dailyInvestmentCancellationDataSource.fetchDailyInvestmentSettingsData()
    }

    override suspend fun fetchDailyInvestmentPauseDetails() = getFlowResult {
        dailyInvestmentCancellationDataSource.fetchDailyInvestmentPauseDetails()
    }

    override suspend fun fetchDailyInvestmentConfirmActionDetails(type: String) = getFlowResult {
        dailyInvestmentCancellationDataSource.fetchDailyInvestmentConfirmActionDetails(type)
    }

    override suspend fun fetchDailyInvestmentPostCancellation() = getFlowResult {
        dailyInvestmentCancellationDataSource.fetchDailyInvestmentPostCancellation()
    }

}