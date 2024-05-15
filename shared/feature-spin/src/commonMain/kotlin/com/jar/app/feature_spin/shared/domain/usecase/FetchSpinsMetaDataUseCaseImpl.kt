package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.app.feature_spin.shared.domain.repository.SpinRepositoryExternal

internal class FetchSpinsMetaDataUseCaseImpl constructor(
    private val spinRepository: SpinRepositoryExternal
) : FetchSpinsMetaDataUseCase {

    override suspend fun fetchSpinsMetaData(includeView: Boolean) =
        spinRepository.fetchSpinsMetaData(includeView)
}