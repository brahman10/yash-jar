package com.jar.app.feature_onboarding.shared.domain.usecase.impl

import com.jar.app.feature_onboarding.shared.domain.model.SavingGoalPostRequest
import com.jar.app.feature_onboarding.shared.domain.repository.LoginRepository
import com.jar.app.feature_onboarding.shared.domain.usecase.PostSavingGoalsUseCase

internal class PostSavingGoalsUseCaseImpl constructor(
    private val loginRepository: LoginRepository
) : PostSavingGoalsUseCase {

    override suspend fun postSavingGoals(reasonForSavingsRequest: com.jar.app.feature_onboarding.shared.domain.model.SavingGoalPostRequest) =
        loginRepository.postSavingGoals(reasonForSavingsRequest)
}