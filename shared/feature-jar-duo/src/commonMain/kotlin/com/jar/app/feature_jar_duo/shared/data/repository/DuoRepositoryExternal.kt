package com.jar.app.feature_jar_duo.shared.data.repository

import com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface DuoRepositoryExternal : BaseRepository {
    suspend fun fetchGroupList(): Flow<RestClientResult<ApiResponseWrapper<List<DuoGroupData>>>>
}