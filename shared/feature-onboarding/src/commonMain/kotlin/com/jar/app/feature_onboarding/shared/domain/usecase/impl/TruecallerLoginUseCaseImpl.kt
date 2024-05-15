package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.feature_onboarding.shared.domain.model.TruecallerLoginRequest
import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.TruecallerLoginUseCase

internal class TruecallerLoginUseCaseImpl constructor(
    private val loginRepository: LoginRepository
) :
    TruecallerLoginUseCase {

    override suspend fun loginViaTruecaller(truecallerLoginRequest: com.jar.app.feature_onboarding.shared.domain.model.TruecallerLoginRequest) =
        loginRepository.loginViaTruecaller(truecallerLoginRequest)
}