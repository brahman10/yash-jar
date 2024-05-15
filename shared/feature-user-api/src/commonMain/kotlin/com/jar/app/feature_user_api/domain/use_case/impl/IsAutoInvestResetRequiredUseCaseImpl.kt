package com.jar.app.feature_user_api.domain.use_case.impl

import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase

internal class IsAutoInvestResetRequiredUseCaseImpl constructor(
    private val userRepository: UserRepository
) : IsAutoInvestResetRequiredUseCase {
    override suspend fun isAutoInvestResetRequired(newAmount: Float, savingsType: String) =
        userRepository.isAutoInvestResetRequired(newAmount, savingsType)
}