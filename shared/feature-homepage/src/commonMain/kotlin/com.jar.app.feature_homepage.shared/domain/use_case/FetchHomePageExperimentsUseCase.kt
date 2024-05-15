package com.jar.app.feature_homepage.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchHomePageExperimentsUseCase {

    suspend fun fetchHomePageExperiments(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

}