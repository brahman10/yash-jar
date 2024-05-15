package com.jar.app.feature_settings.domain.use_case.impl

import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.use_case.FetchVpaChipUseCase

internal class FetchVpaChipUseCaseImpl constructor(private val settingsRepository: SettingsRepository) :
    FetchVpaChipUseCase {

    override suspend fun fetchVpaChips() = settingsRepository.fetchVpaChips()
}