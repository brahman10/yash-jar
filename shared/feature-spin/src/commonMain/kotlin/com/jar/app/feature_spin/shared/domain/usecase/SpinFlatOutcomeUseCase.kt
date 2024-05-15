package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_spin.shared.domain.model.FlatOutcome
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

interface SpinFlatOutcomeUseCase {
    suspend fun fetchSpinFlatOutCome(spinId: JsonObject, flowTypeContext: SpinsContextFlowType): Flow<RestClientResult<ApiResponseWrapper<FlatOutcome?>>>
}