package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.app.feature_spin.shared.domain.repository.SpinRepositoryExternal

internal class FetchSpinIntroUseCaseImpl constructor(
    private val spinRepository: SpinRepositoryExternal
) : FetchSpinIntroUseCase {
    override suspend fun fetchSpinIntro() = spinRepository.fetchIntroPageData()
}