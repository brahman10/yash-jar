package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase

internal class FetchUserGoldBalanceUseCaseImpl constructor(
    private val userRepository: UserRepository
) : FetchUserGoldBalanceUseCase {

    override suspend fun fetchUserGoldBalance(includeView: Boolean) = userRepository.fetchUserGoldBalance(includeView)
}