package com.jar.app.feature_spin.shared.domain.repository

import com.jar.app.core_base.domain.model.JackPotResponseV2
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import com.jar.app.feature_spin.shared.domain.model.FlatOutcome
import com.jar.app.feature_spin.shared.domain.model.GameModelRequest
import com.jar.app.feature_spin.shared.domain.model.GameResult
import com.jar.app.feature_spin.shared.domain.model.SpinToWinResponse
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

interface SpinRepositoryInternal : BaseRepository {

    suspend fun fetchSpinsData(
        flowTypeContext: SpinsContextFlowType
    ): Flow<RestClientResult<ApiResponseWrapper<SpinToWinResponse>>>

    suspend fun fetchSpinResultData(
        gameModelRequest: GameModelRequest?,
        flowTypeContext: SpinsContextFlowType
    ): Flow<RestClientResult<ApiResponseWrapper<GameResult>>>

    suspend fun fetchSpinFlatOutcomeData(
        jsonObject: JsonObject,
        flowTypeContext: SpinsContextFlowType
    ): Flow<RestClientResult<ApiResponseWrapper<FlatOutcome?>>>

    suspend fun fetchSpinJackpotOutComeData(
        jsonObject: JsonObject,
        flowTypeContext: SpinsContextFlowType
    ): Flow<RestClientResult<ApiResponseWrapper<JackPotResponseV2>>>

    suspend fun resetSpin(spinId: String): RestClientResult<ApiResponseWrapper<Unit?>>
}