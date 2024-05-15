package com.jar.app.feature_settings.domain.use_case.impl

import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.model.DeleteCard
import com.jar.app.feature_settings.domain.use_case.DeleteSavedCardUseCase

internal class DeleteSavedCardUseCaseImpl constructor(
    private val settingsRepository: SettingsRepository
) : DeleteSavedCardUseCase {
    override suspend fun deleteSavedCardUseCase(deleteCard: DeleteCard) = settingsRepository.deleteSavedCard(deleteCard)
}