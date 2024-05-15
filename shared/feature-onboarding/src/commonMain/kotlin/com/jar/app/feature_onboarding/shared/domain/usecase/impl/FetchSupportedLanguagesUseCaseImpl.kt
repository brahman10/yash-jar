package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSupportedLanguagesUseCase

internal class FetchSupportedLanguagesUseCaseImpl constructor(private val loginRepository: LoginRepository) :
    FetchSupportedLanguagesUseCase {

    override suspend fun fetchSupportedLanguages() = loginRepository.fetchSupportedLanguages()
}