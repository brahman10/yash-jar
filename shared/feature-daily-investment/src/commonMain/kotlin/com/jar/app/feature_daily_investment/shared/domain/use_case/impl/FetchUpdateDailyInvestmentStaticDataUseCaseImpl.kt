package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchUpdateDailyInvestmentStaticDataUseCase


internal class FetchUpdateDailyInvestmentStaticDataUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : FetchUpdateDailyInvestmentStaticDataUseCase {

    override suspend fun fetchUpdateDailyInvestmentStaticData() =
        dailyInvestmentRepository.fetchUpdateDailyInvestmentStaticData()
}