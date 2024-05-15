package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDSAmountSelectionUseCase


internal class FetchDSAmountSelectionUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : FetchDSAmountSelectionUseCase {
    override suspend fun fetchAmountSelectionScreenData() =
        dailyInvestmentRepository.fetchAmountSelectionScreenData()
}