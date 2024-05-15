package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.app.feature_spin.shared.domain.model.SpinsMetaData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchSpinsMetaDataUseCase {

    suspend fun fetchSpinsMetaData(includeView: Boolean = false): Flow<RestClientResult<ApiResponseWrapper<SpinsMetaData>>>

}