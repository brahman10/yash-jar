package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.RequestOtpUseCase

internal class RequestOtpUseCaseImpl constructor(
    private val loginRepository: LoginRepository
) :
    RequestOtpUseCase {

    override suspend fun requestOtp(
        hasExperianConsent: Boolean,
        phoneNumber: String,
        countryCode: String
    ) =
        loginRepository.requestOTP(hasExperianConsent, phoneNumber, countryCode)

    override suspend fun requestOtpViaCall(phoneNumber: String, countryCode: String) =
        loginRepository.requestOTPViaCall(phoneNumber, countryCode)
}