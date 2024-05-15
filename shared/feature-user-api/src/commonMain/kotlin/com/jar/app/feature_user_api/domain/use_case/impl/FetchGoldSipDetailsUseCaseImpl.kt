package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase

internal class FetchGoldSipDetailsUseCaseImpl constructor(
    private val userRepository: UserRepository
) : FetchGoldSipDetailsUseCase {

    override suspend fun fetchGoldSipDetails(includeView: Boolean) = userRepository.fetchGoldSipDetails(includeView)
}