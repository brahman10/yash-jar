package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.ValidateAddressPinCodeUseCase

internal class ValidateAddressPinCodeUseCaseImpl constructor(
    private val userRepository: UserRepository
) : ValidateAddressPinCodeUseCase {

    override suspend fun validatePinCode(pinCode: String) = userRepository.validatePinCode(pinCode)
}