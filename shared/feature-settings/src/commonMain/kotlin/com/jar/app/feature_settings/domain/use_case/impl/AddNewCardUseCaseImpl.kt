package com.jar.app.feature_settings.domain.use_case.impl

import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.model.CardDetail
import com.jar.app.feature_settings.domain.use_case.AddNewCardUseCase

internal class AddNewCardUseCaseImpl constructor(private val settingsRepository: SettingsRepository) :
    AddNewCardUseCase {
    override suspend fun addNewCard(cardDetail: CardDetail) = settingsRepository.addNewCard(cardDetail)
}