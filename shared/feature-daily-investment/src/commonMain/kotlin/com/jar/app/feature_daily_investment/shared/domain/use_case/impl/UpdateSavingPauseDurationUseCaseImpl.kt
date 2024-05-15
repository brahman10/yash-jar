package com.jar.app.feature_daily_investment.shared.domain.use_case.impl

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_daily_investment.shared.data.repository.DailyInvestmentRepository
import com.jar.app.feature_daily_investment.shared.domain.use_case.UpdateSavingPauseDurationUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType

internal class UpdateSavingPauseDurationUseCaseImpl constructor(
    private val dailyInvestmentRepository: DailyInvestmentRepository
) : UpdateSavingPauseDurationUseCase {

    override suspend fun updateSavingPauseDuration(
        pause: Boolean,
        pauseDuration: String?,
        savingType: SavingsType,
        customDuration: Long?
    ) = dailyInvestmentRepository.updateSavingPauseDuration(pause, pauseDuration, savingType, customDuration)
}