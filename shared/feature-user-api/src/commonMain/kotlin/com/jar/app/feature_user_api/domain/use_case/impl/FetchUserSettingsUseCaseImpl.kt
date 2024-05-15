package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.FetchUserSettingsUseCase

internal class FetchUserSettingsUseCaseImpl constructor(
    private val userRepository: UserRepository
) : FetchUserSettingsUseCase {

    override suspend fun fetchUserSettings() = userRepository.fetchUserSettings()

}