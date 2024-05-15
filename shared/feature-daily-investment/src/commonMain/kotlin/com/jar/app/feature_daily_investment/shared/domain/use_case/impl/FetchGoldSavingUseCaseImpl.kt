package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchGoldSavingUseCase

class FetchGoldSavingUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : FetchGoldSavingUseCase {
    override suspend fun fetchDailyInvestedGoldSaving()  =
        dailyInvestmentRepository.fetchDailyGoldInvestedSavings()
}