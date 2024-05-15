package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.feature_user_api.domain.model.OTPLoginRequest
import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.OtpLoginUseCase

internal class OtpLoginUseCaseImpl constructor(
    private val loginRepository: LoginRepository
) : OtpLoginUseCase {

    override suspend fun loginViaOtp(otpLoginRequest: OTPLoginRequest) =
        loginRepository.loginViaOtp(otpLoginRequest)
}