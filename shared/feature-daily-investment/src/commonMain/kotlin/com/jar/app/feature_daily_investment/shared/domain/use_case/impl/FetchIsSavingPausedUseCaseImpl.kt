package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchIsSavingPausedUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType

internal class FetchIsSavingPausedUseCaseImpl constructor(private val dailyInvestmentRepository: DailyInvestmentRepository) :
    FetchIsSavingPausedUseCase {

    override suspend fun fetchIsSavingPaused(
        savingsType: SavingsType,
        includeView: Boolean
    ) = dailyInvestmentRepository.fetchIsSavingPaused(savingsType, includeView)
}