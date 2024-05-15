package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.app.feature_spin.shared.domain.model.GameModelRequest
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_spin.shared.domain.model.GameResult
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import kotlinx.coroutines.flow.Flow

interface FetchSpinsResultDataUseCase {

    suspend fun fetchSpinsResultData(gameId: GameModelRequest? = null, flowTypeContext: SpinsContextFlowType): Flow<RestClientResult<ApiResponseWrapper<GameResult>>>

}