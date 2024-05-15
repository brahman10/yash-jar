package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchOTPStatusUseCase

internal class FetchOTPStatusUseCaseImpl constructor(
    private val loginRepository: LoginRepository
) : FetchOTPStatusUseCase {
    override suspend fun fetchOTPStatus(phoneNumber: String,countryCode: String) =
        loginRepository.fetchOTPStatus(phoneNumber,countryCode)

}