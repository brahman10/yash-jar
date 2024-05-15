package com.jar.app.feature.home.domain.usecase

import com.jar.app.feature.home.domain.model.UserRatingData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchUserRatingUseCase {

    suspend fun getUserRating(): Flow<RestClientResult<ApiResponseWrapper<UserRatingData?>>>

}