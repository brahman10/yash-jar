package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentOnboardingStoryUseCase


class FetchDailyInvestmentOnboardingStoryUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : FetchDailyInvestmentOnboardingStoryUseCase {

    override suspend fun fetchDailyInvestmentStoriesData() =
        dailyInvestmentRepository.fetchDailyInvestmentStoriesData()

}
