package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.app.core_base.domain.model.JackPotResponseV2
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

interface FetchJackpotOutComeDataUseCase {
    suspend fun fetchJackpotOutComeData(spinId: JsonObject, flowTypeContext: SpinsContextFlowType): Flow<RestClientResult<ApiResponseWrapper<JackPotResponseV2>>>
}