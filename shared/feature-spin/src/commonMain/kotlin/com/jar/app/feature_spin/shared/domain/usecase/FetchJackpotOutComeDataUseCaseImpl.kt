package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import com.jar.app.feature_spin.shared.domain.repository.SpinRepositoryInternal
import kotlinx.serialization.json.JsonObject

internal class FetchJackpotOutComeDataUseCaseImpl constructor(
    private val spinRepository: SpinRepositoryInternal
) : FetchJackpotOutComeDataUseCase {
    override suspend fun fetchJackpotOutComeData(spinId: JsonObject, flowTypeContext: SpinsContextFlowType) =
        spinRepository.fetchSpinJackpotOutComeData(spinId, flowTypeContext)
}