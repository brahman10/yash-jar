package com.jar.app.feature_jar_duo.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupInfo
import com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page.DuoGroupInfoV2
import kotlinx.coroutines.flow.Flow

interface FetchGroupInfoUseCase {
    suspend fun fetchGroupInfo(groupId: String?) : Flow<RestClientResult<ApiResponseWrapper<DuoGroupInfo>>>

    suspend fun fetchGroupInfoV2(groupId: String?) : Flow<RestClientResult<ApiResponseWrapper<DuoGroupInfoV2>>>
}