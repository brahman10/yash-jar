package com.jar.app.feature_settings.domain.use_case.impl

import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.use_case.AddNewUpiIdUseCase

internal class AddNewUpiIdUseCaseImpl constructor(private val settingsRepository: SettingsRepository) :
    AddNewUpiIdUseCase {
    override suspend fun addNewUpiId(upiId: String) = settingsRepository.addNewUpiId(upiId)
}