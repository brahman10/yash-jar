package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchUserSavingPreferencesUseCase

internal class FetchUserSavingPreferencesUseCaseImpl constructor(
    private val repository: LoginRepository
) : FetchUserSavingPreferencesUseCase {
    override suspend fun getUserSavingPreferences() = repository.getUserSavingPreferences()
}