package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.GetUserSavedAddressUseCase

internal class GetUserSavedAddressUseCaseImpl constructor(private val userRepository: UserRepository) :
    GetUserSavedAddressUseCase {
    override suspend fun getSavedAddress() = userRepository.getAllAddress()
}