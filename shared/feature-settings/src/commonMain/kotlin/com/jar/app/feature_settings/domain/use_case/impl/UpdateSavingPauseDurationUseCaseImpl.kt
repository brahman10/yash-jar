package com.jar.app.feature_settings.domain.use_case.impl

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.use_case.UpdateSavingPauseDurationUseCase

internal class UpdateSavingPauseDurationUseCaseImpl constructor(
    private val settingsRepository: SettingsRepository
) : UpdateSavingPauseDurationUseCase {

    override suspend fun updateSavingPauseDuration(
        pause: Boolean,
        pauseDuration: String?,
        savingType: SavingsType
    ) = settingsRepository.updateSavingPauseDuration(pause, pauseDuration, savingType)
}