package com.jar.app.feature_weekly_magic_common.shared.domain.repository

import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeDetail
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.data.BaseRepositoryV2
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface WeeklyChallengeRepositoryExternal: BaseRepositoryV2 {

    suspend fun fetchWeeklyChallengeDetail(challengeId: String? = null): Flow<RestClientResult<ApiResponseWrapper<WeeklyChallengeDetail?>>>

    suspend fun fetchWeeklyChallengesMetaData(includeView: Boolean): Flow<RestClientResult<ApiResponseWrapper<WeeklyChallengeMetaData?>>>

    suspend fun markCurrentWeeklyChallengeAsViewed(challengeId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun markPreviousWeeklyChallengeAsViewed(challengeId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun markPreviousWeeklyChallengeStoryAsViewed(challengeId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun markWeeklyChallengeOnBoarded(): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}