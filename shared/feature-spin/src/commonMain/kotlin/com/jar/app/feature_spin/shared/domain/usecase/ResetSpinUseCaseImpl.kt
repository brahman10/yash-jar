package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.app.feature_spin.shared.domain.repository.SpinRepositoryInternal

internal class ResetSpinUseCaseImpl constructor(private val spinRepositoryInternal: SpinRepositoryInternal) :
    ResetSpinsUseCase {
    override suspend fun resetSpin(spinId: String) = spinRepositoryInternal.resetSpin(spinId)
}