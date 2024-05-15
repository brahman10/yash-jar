package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchOtlUserInfoUseCase
import com.jar.app.feature_user_api.domain.model.OTLLoginRequest

class FetchOtlUserInfoUseCaseImpl constructor(
    private val loginRepository: LoginRepository
) : FetchOtlUserInfoUseCase {
    override suspend fun fetchOtlUserInfo(
        otlLoginRequest: OTLLoginRequest
    ) = loginRepository.fetchOtlUserInfo(otlLoginRequest)

}