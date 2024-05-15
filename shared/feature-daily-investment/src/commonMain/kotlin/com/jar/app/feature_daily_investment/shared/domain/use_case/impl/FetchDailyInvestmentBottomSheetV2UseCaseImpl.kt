package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailyInvestmentBottomSheetV2UseCase

internal class FetchDailyInvestmentBottomSheetV2UseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : FetchDailyInvestmentBottomSheetV2UseCase {
    override suspend fun fetchDailyInvestmentBottomSheetV2Data() =
        dailyInvestmentRepository.fetchDailyInvestmentBottomSheetV2Data()
}