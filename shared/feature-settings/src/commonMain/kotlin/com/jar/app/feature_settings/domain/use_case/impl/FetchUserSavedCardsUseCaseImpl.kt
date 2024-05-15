package com.jar.app.feature_settings.domain.use_case.impl

import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.use_case.FetchUserSavedCardsUseCase

internal class FetchUserSavedCardsUseCaseImpl constructor(private val settingsRepository: SettingsRepository) :
    FetchUserSavedCardsUseCase {
    override suspend fun fetchSavedCards() = settingsRepository.fetchSavedCards()
}