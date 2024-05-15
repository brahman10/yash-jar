package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDSAbandonScreenUseCase


internal class FetchDSAbandonScreenUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : FetchDSAbandonScreenUseCase {

    override suspend fun fetchAbandonBottomSheetData(contentType : String) =
        dailyInvestmentRepository.fetchAbandonBottomSheetData(contentType)

}