package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchOnboardingStoriesUseCase

internal class FetchOnboardingStoriesUseCaseImpl constructor(
    private val loginRepository: LoginRepository
) : FetchOnboardingStoriesUseCase {

    override suspend fun fetchOnboardingStories() = loginRepository.fetchOnboardingStories()
}