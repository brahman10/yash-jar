package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.FetchUserMetaUseCase

internal class FetchUserMetaUseCaseImpl constructor(
    private val userRepository: UserRepository,
) : FetchUserMetaUseCase {

    override suspend fun fetchUserMeta() = userRepository.fetchRemoteUserMetaData()

}