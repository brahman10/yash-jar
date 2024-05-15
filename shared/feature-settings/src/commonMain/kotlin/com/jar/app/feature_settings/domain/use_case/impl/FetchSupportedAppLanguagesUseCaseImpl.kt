package com.jar.app.feature_settings.domain.use_case.impl

import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.use_case.FetchSupportedAppLanguagesUseCase

internal class FetchSupportedAppLanguagesUseCaseImpl constructor(
    private val settingsRepository: SettingsRepository
): FetchSupportedAppLanguagesUseCase {

    override suspend fun fetchSupportedLanguages() = settingsRepository.fetchSupportedLanguages()

}