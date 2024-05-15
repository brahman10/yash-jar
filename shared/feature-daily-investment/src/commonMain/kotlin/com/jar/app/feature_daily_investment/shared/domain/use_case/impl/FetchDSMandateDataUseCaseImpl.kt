package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDSMandateDataUseCase

internal class FetchDSMandateDataUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : FetchDSMandateDataUseCase {

    override suspend fun fetchDSMandateBSData() =
        dailyInvestmentRepository.fetchDailySavingsMandateBottomSheetData()
}