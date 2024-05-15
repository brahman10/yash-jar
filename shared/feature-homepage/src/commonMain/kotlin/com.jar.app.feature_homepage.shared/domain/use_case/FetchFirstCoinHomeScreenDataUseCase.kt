package com.jar.app.feature_homepage.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_homepage.shared.domain.model.FirstCoinHomeScreenData
import kotlinx.coroutines.flow.Flow


interface FetchFirstCoinHomeScreenDataUseCase {

    suspend fun fetchFirstCoinHomeScreenData(): Flow<RestClientResult<ApiResponseWrapper<FirstCoinHomeScreenData?>>>

}