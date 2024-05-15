package com.jar.app.feature_homepage.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_homepage.shared.domain.model.HelpVideosResponse
import kotlinx.coroutines.flow.Flow

interface FetchHelpVideosUseCase {

    suspend fun fetchHelpVideos(
        language: String,
        includeView: Boolean = false
    ): Flow<RestClientResult<ApiResponseWrapper<HelpVideosResponse>>>
}