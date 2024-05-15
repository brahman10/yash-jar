package com.jar.app.feature_savings_common.shared.domain.use_case.impl

import com.jar.app.feature_savings_common.shared.data.repository.SavingsCommonRepository
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase

internal class ManageSavingPreferenceUseCaseImpl constructor(private val savingsCommonRepository: SavingsCommonRepository) :
    ManageSavingPreferenceUseCase {
    override suspend fun manageSavingsPreference(
        savingsType: SavingsType,
        enableAutoSave: Boolean
    ) = savingsCommonRepository.manageSavingsPreference(savingsType, enableAutoSave)
}