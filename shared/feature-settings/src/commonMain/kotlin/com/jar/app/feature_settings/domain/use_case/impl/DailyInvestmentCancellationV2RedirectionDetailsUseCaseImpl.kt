package com.jar.app.feature_settings.domain.use_case.impl

import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.use_case.DailyInvestmentCancellationV2RedirectionDetailsUseCase

internal class DailyInvestmentCancellationV2RedirectionDetailsUseCaseImpl constructor(
    private val settingsRepository: SettingsRepository
): DailyInvestmentCancellationV2RedirectionDetailsUseCase {
    override suspend fun fetchDailySavingRedirectionDetails() = settingsRepository.fetchDailySavingRedirectionDetails()
}