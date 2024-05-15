package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchExperianTCUseCase

class FetchExperianTCUseCaseImpl constructor(
    private val loginRepository: LoginRepository
) : FetchExperianTCUseCase {
    override suspend fun fetchExperianTC() =
        loginRepository.getExperianTC()

}