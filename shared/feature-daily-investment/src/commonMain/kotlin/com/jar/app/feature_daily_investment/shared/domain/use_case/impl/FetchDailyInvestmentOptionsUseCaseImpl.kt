package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentOptionsUseCase

internal class FetchDailyInvestmentOptionsUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : FetchDailyInvestmentOptionsUseCase {

    override suspend fun fetchDailyInvestmentOptions(context: String?) =
        dailyInvestmentRepository.fetchDailyInvestmentOptions(context)

}