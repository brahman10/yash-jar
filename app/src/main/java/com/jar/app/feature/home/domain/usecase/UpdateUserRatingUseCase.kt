package com.jar.app.feature.home.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

interface UpdateUserRatingUseCase {

    suspend fun submitUserRating(json: JsonObject): Flow<RestClientResult<ApiResponseWrapper<String>>>

}