package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.domain.use_case.EditUserAddressUseCase

internal class EditUserAddressUseCaseImpl constructor(
    private val userRepository: UserRepository
): EditUserAddressUseCase {

    override suspend fun editAddress(
        id: String,
        address: Address
    ) = userRepository.editAddress(id, address)
}