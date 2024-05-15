package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailySavingsFaqDataUseCase


class FetchDailySavingsFaqDataUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : FetchDailySavingsFaqDataUseCase {

    override suspend fun fetchDailySavingsFaqData() = dailyInvestmentRepository.fetchDailySavingsFaqData()

}