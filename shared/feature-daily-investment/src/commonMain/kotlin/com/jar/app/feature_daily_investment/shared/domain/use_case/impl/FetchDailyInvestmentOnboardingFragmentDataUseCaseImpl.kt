package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentOnboardingFragmentDataUseCase

class FetchDailyInvestmentOnboardingFragmentDataUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : FetchDailyInvestmentOnboardingFragmentDataUseCase {

    override suspend fun fetchDailyInvestmentOnboardingFragmentData(version: String?) =
        dailyInvestmentRepository.fetchDailyInvestmentOnboardingData(version)

}