package com.jar.app.feature_weekly_magic_common.shared.domain.usecase

import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchWeeklyChallengeMetaDataUseCase {

    suspend fun fetchWeeklyChallengeMetaData(includeView: Boolean): Flow<RestClientResult<ApiResponseWrapper<WeeklyChallengeMetaData?>>>

}