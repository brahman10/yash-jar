package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.app.feature_spin.shared.domain.model.SpinToWinResponse
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchSpinDataUseCase {

    suspend fun fetchSpinsData(flowType: SpinsContextFlowType): Flow<RestClientResult<ApiResponseWrapper<SpinToWinResponse>>>

}