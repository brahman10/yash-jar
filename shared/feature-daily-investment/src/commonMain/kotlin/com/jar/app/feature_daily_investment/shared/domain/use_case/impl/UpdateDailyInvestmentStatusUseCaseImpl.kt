package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateDailyInvestmentStatusUseCase

internal class UpdateDailyInvestmentStatusUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : UpdateDailyInvestmentStatusUseCase {

    override suspend fun updateDailyInvestmentStatus(amount: Float?, disable: Boolean?) =
        dailyInvestmentRepository.updateDailyInvestmentStatus(amount, disable)
}