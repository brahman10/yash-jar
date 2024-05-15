package com.jar.app.feature_settings.domain.use_case.impl

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.use_case.FetchIsSavingPausedUseCase

internal class FetchIsSavingPausedUseCaseImpl constructor(private val settingsRepository: SettingsRepository) :
    FetchIsSavingPausedUseCase {
    override suspend fun fetchIsSavingPaused(pauseType: SavingsType)
    = settingsRepository.fetchIsSavingPaused(pauseType)
}