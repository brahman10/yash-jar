package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSavingGoalsV2UseCase

internal class FetchSavingGoalsV2UseCaseImpl constructor(
    private val repository: LoginRepository
) : FetchSavingGoalsV2UseCase {
    override suspend fun fetchSavingGoals() = repository.fetchSavingGoalsV2()
}
