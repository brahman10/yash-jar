package com.jar.app.feature_savings_common.shared.domain.use_case.impl

import com.jar.app.feature_savings_common.shared.data.repository.SavingsCommonRepository
import com.jar.app.feature_savings_common.shared.domain.model.UpdateUserSavingRequest
import com.jar.app.feature_savings_common.shared.domain.use_case.UpdateUserSavingUseCase


internal class UpdateUserSavingUseCaseImpl constructor(
    private val savingsCommonRepository: SavingsCommonRepository
) : UpdateUserSavingUseCase {

    override suspend fun updateUserSavings(updateUserSavingRequest: UpdateUserSavingRequest) =
        savingsCommonRepository.updateUserSavings(updateUserSavingRequest)
}