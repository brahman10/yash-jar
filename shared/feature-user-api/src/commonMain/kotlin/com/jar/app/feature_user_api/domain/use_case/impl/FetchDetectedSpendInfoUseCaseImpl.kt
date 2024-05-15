package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.FetchDetectedSpendInfoUseCase

internal class FetchDetectedSpendInfoUseCaseImpl constructor(private val userRepository: UserRepository) :
    FetchDetectedSpendInfoUseCase {

    override suspend fun fetchDetectedSpendInfo(includeView: Boolean) =
        userRepository.fetchDetectedSpendInfo(includeView)

}