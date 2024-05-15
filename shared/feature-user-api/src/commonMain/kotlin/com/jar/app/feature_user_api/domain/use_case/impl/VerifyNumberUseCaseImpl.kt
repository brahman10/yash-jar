package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.model.OTPLoginRequest
import com.jar.app.feature_user_api.domain.use_case.VerifyNumberUseCase

internal class VerifyNumberUseCaseImpl constructor(
    private val userRepository: UserRepository
) : VerifyNumberUseCase {

    override suspend fun verifyPhoneNumber(otpLoginRequest: OTPLoginRequest) =
        userRepository.verifyPhoneNumber(otpLoginRequest)
}