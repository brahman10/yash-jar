package com.jar.app.feature_savings_common.shared.domain.use_case.impl

import com.jar.app.feature_savings_common.shared.data.repository.SavingsCommonRepository
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.use_case.DisableUserSavingsUseCase


internal class DisableUserSavingsUseCaseImpl constructor(
    private val savingsCommonRepository: SavingsCommonRepository
) : DisableUserSavingsUseCase {

    override suspend fun disableSavings(savingsType: SavingsType) =
        savingsCommonRepository.disableSavings(savingsType)
}