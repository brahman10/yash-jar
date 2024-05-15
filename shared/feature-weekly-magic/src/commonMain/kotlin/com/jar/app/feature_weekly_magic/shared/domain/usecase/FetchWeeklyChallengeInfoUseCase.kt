package com.jar.app.feature_weekly_magic.shared.domain.usecase

import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeInfo
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchWeeklyChallengeInfoUseCase {

    suspend fun fetchWeeklyChallengeInfo(): Flow<RestClientResult<ApiResponseWrapper<WeeklyChallengeInfo?>>>

}