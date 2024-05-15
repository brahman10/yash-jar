package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailySavingsIntroBottomSheetUseCase

internal class FetchDailySavingsIntroBottomSheetUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : FetchDailySavingsIntroBottomSheetUseCase {

    override suspend fun fetchDailySavingsInto() = dailyInvestmentRepository.fetchDailySavingsIntroBottomSheetData()

}