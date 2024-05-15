package com.jar.app.feature_spin.shared.data.repository

import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import com.jar.app.feature_spin.shared.data.network.SpinDataSource
import com.jar.app.feature_spin.shared.domain.model.GameModelRequest
import com.jar.app.feature_spin.shared.domain.model.SpinsMetaData
import com.jar.app.feature_spin.shared.domain.repository.SpinRepositoryExternal
import com.jar.app.feature_spin.shared.domain.repository.SpinRepositoryInternal
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

internal class SpinRepositoryImpl constructor(
    private val spinDataSource: SpinDataSource
) : SpinRepositoryInternal, SpinRepositoryExternal {
    override suspend fun fetchSpinsMetaData(includeView: Boolean):
            Flow<RestClientResult<ApiResponseWrapper<SpinsMetaData>>> =
        getFlowResult {
            spinDataSource.fetchSpinsMetaData(includeView)
        }

    override suspend fun fetchSpinsData(flowTypeContext: SpinsContextFlowType) =
        getFlowResult {
            spinDataSource.fetchSpinData(flowTypeContext)
        }

    override suspend fun fetchSpinResultData(gameModelRequest: GameModelRequest?, flowTypeContext: SpinsContextFlowType)=
        getFlowResult {
            spinDataSource.fetchSpinResult(gameModelRequest, flowTypeContext)
        }

    override suspend fun fetchSpinFlatOutcomeData(jsonObject: JsonObject, flowTypeContext: SpinsContextFlowType)=
        getFlowResult {
            spinDataSource.fetchFlatOutComeResult(jsonObject, flowTypeContext)
        }

    override suspend fun fetchSpinJackpotOutComeData(
        jsonObject: JsonObject,
        flowTypeContext: SpinsContextFlowType
    ) =
        getFlowResult {
            spinDataSource.fetchJackpotOutComeResult(jsonObject, flowTypeContext)
        }

    override suspend fun resetSpin(spinId: String) =
        spinDataSource.resetSpin(spinId)

    override suspend fun fetchIntroPageData() =
        getFlowResult {
            spinDataSource.fetchSpinIntro()
        }

    override suspend fun fetchUseWinning() =
        getFlowResult {
            spinDataSource.fetchUseWinning()
        }
}