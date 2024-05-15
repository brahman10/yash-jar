package com.jar.app.feature_settings.domain.use_case.impl

import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.use_case.FetchCardBinInfoUseCase

internal class FetchCardBinInfoUseCaseImpl constructor(private val settingsRepository: SettingsRepository) :
    FetchCardBinInfoUseCase {
    override suspend fun fetchCardBinInfo(cardBin: String) = settingsRepository.fetchCardBinInfo(cardBin)
}