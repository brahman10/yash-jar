package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.app.feature_spin.shared.domain.repository.SpinRepositoryInternal
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import kotlinx.serialization.json.JsonObject

internal class SpinFlatOutcomeUseCaseImpl constructor(
    private val spinRepository: SpinRepositoryInternal
) : SpinFlatOutcomeUseCase {
    override suspend fun fetchSpinFlatOutCome(
        spinId: JsonObject, flowTypeContext: SpinsContextFlowType
    ) = spinRepository.fetchSpinFlatOutcomeData(spinId, flowTypeContext)
}