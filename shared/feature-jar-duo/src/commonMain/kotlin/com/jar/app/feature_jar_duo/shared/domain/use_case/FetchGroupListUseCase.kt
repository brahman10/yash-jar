package com.jar.app.feature_jar_duo.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData
import kotlinx.coroutines.flow.Flow

interface FetchGroupListUseCase {
    suspend fun fetchGroupList() : Flow<RestClientResult<ApiResponseWrapper<List<DuoGroupData>>>>
}