package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.app.feature_spin.shared.domain.repository.SpinRepositoryExternal

internal class FetchUseWinningUseCaseImpl constructor(
    private val spinRepository: SpinRepositoryExternal
): FetchUseWinningUseCase {
    override suspend fun fetchUseWinning() = spinRepository.fetchUseWinning()
}