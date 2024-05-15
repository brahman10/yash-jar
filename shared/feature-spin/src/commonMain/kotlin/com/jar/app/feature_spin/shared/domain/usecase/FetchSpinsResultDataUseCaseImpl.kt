package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import com.jar.app.feature_spin.shared.domain.model.GameModelRequest
import com.jar.app.feature_spin.shared.domain.repository.SpinRepositoryInternal

internal class FetchSpinsResultDataUseCaseImpl constructor(
    private val spinRepository: SpinRepositoryInternal
) : FetchSpinsResultDataUseCase {

    override suspend fun fetchSpinsResultData(gameModelRequest: GameModelRequest?, flowTypeContext: SpinsContextFlowType) =
        spinRepository.fetchSpinResultData(gameModelRequest, flowTypeContext)
}