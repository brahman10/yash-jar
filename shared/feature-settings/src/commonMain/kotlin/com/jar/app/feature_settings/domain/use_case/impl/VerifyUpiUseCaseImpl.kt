package com.jar.app.feature_settings.domain.use_case.impl

import com.jar.app.feature_settings.data.repository.SettingsRepository
import com.jar.app.feature_settings.domain.use_case.VerifyUpiUseCase

internal class VerifyUpiUseCaseImpl constructor(
    private val settingsRepository: SettingsRepository
) : VerifyUpiUseCase {

    override suspend fun verifyUpiAddress(upiAddress: String) =
        settingsRepository.verifyUpiAddress(upiAddress)
}