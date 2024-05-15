package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.DeleteUserAddressUseCase

internal class DeleteUserAddressUseCaseImpl constructor(private val userRepository: UserRepository) :
    DeleteUserAddressUseCase {

    override suspend fun deleteAddress(id: String) = userRepository.deleteAddress(id)
}