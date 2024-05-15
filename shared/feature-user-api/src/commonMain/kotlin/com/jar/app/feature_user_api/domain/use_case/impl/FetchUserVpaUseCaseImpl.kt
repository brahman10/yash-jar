package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.FetchUserVpaUseCase

internal class FetchUserVpaUseCaseImpl constructor(private val userRepository: UserRepository) :
    FetchUserVpaUseCase {

    override suspend fun fetchUserSavedVPAs() = userRepository.fetchUserSavedVPAs()

}