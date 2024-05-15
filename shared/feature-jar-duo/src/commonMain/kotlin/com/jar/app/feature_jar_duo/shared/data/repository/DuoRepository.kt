package com.jar.app.feature_jar_duo.shared.data.repository

import com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupInfo
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_intro_story.DuoIntroData
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupInfoV2
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface DuoRepository : BaseRepository {

    suspend fun fetchGroupInfo(groupId: String?): Flow<RestClientResult<ApiResponseWrapper<DuoGroupInfo>>>

    suspend fun fetchGroupInfoV2(groupId: String?): Flow<RestClientResult<ApiResponseWrapper<DuoGroupInfoV2>>>

    suspend fun renameGroup(
        groupId: String,
        groupName: String
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun deleteGroup(groupId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchDuoIntroStory(): Flow<RestClientResult<ApiResponseWrapper<DuoIntroData>>>
}