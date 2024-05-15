package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.app.feature_spin.shared.domain.repository.SpinRepositoryInternal
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType


internal class FetchSpinDataUseCaseImpl constructor(
    private val spinRepository: SpinRepositoryInternal
) : FetchSpinDataUseCase {
    override suspend fun fetchSpinsData(flowTypeContext: SpinsContextFlowType) =
        spinRepository.fetchSpinsData(flowTypeContext)
}