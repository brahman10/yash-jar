package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentStatusUseCase

internal class FetchDailyInvestmentStatusUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : FetchDailyInvestmentStatusUseCase {

    override suspend fun fetchDailyInvestmentStatus(includeView: Boolean) = dailyInvestmentRepository.fetchDailyInvestmentStatus(includeView)
}