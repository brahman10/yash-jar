package com.jar.app.feature_profile.domain.use_case.impl

import com.jar.app.feature_profile.domain.use_case.RequestOtpUseCase

internal class RequestOtpUseCaseImpl constructor(private val userRepository: com.jar.app.feature_profile.data.repository.UserRepository) :
    RequestOtpUseCase {

    override suspend fun requestOtp(phoneNumber: String, countryCode: String) =
        userRepository.requestOTP(phoneNumber, countryCode)

    override suspend fun requestOtpViaCall(phoneNumber: String, countryCode: String) =
        userRepository.requestOTPViaCall(phoneNumber, countryCode)
}