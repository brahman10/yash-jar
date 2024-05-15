package com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment_cancellation.shared.data.repository.DailyInvestmentCancellationRepository
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.FetchDailyInvestmentSettingsDataUseCase


internal class FetchDailyInvestmentSettingsDataUseCaseImpl constructor(
    private val dailyInvestmentCancellationRepository: DailyInvestmentCancellationRepository
) : FetchDailyInvestmentSettingsDataUseCase {
    override suspend fun fetchDailyInvestmentSettingsData() = dailyInvestmentCancellationRepository.fetchDailyInvestmentSettingsData()

}
