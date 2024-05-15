package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSavingGoalsUseCase

internal class FetchSavingGoalsUseCaseImpl constructor(
    private val repository: LoginRepository
) : FetchSavingGoalsUseCase {
    override suspend fun fetchSavingGoals() = repository.fetchSavingGoals()
}