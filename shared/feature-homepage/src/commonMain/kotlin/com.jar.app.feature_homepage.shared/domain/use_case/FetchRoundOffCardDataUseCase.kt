package com.jar.app.feature_homepage.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_homepage.shared.domain.model.round_off.RoundOffData
import kotlinx.coroutines.flow.Flow

interface FetchRoundOffCardDataUseCase {

    suspend fun fetchRoundOffCardData(): Flow<RestClientResult<ApiResponseWrapper<RoundOffData?>>>
}