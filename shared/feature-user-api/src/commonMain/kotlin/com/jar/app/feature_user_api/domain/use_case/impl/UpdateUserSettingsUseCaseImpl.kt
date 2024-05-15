package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.dto.UserSettingsDTO
import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.UpdateUserSettingsUseCase

internal class UpdateUserSettingsUseCaseImpl constructor(
    private val userRepository: UserRepository
) : UpdateUserSettingsUseCase {

    override suspend fun updateUserSettings(userSettings: UserSettingsDTO) =
        userRepository.updateUserSettings(userSettings)
}