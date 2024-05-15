package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDSEducationUseCase

class FetchDSEducationUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : FetchDSEducationUseCase {
    override suspend fun fetchDSEducationData() = dailyInvestmentRepository.fetchDSEducationData()
}