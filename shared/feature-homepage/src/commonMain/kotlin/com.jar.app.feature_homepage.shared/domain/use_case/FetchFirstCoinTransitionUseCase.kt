package com.jar.app.feature_homepage.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_homepage.shared.domain.model.FirstCoinTransitionData
import kotlinx.coroutines.flow.Flow

interface FetchFirstCoinTransitionUseCase {

    suspend fun fetchFirstCoinTransitionPageData(): Flow<RestClientResult<ApiResponseWrapper<FirstCoinTransitionData>>>

}