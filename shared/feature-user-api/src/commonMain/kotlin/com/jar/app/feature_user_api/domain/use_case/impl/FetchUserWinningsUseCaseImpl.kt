package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.FetchUserWinningsUseCase

internal class FetchUserWinningsUseCaseImpl constructor(private val userRepository: UserRepository) :
    FetchUserWinningsUseCase {

    override suspend fun fetchUserWinnings() = userRepository.fetchUserWinnings()
}