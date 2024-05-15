package com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment_cancellation.shared.data.repository.DailyInvestmentCancellationRepository
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.FetchDailyInvestmentPauseDataUseCase

internal class FetchDailyInvestmentPauseDataUseCaseImpl constructor(
    private val dailyInvestmentCancellationRepository: DailyInvestmentCancellationRepository
) : FetchDailyInvestmentPauseDataUseCase {
    override suspend fun fetchDailyInvestmentPauseData() = dailyInvestmentCancellationRepository.fetchDailyInvestmentPauseDetails()

}