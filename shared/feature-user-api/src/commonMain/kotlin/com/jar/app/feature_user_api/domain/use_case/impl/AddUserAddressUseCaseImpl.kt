package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.domain.use_case.AddUserAddressUseCase

internal class AddUserAddressUseCaseImpl constructor(
    private val userRepository: UserRepository
) : AddUserAddressUseCase {

    override suspend fun addDeliveryAddress(address: Address) =
        userRepository.addDeliveryAddress(address)

}