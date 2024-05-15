package com.jar.app.feature_post_setup.domain.use_case

import com.jar.app.feature_post_setup.domain.model.setting.PostSetupQuickActionList
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchPostSetupQuickActionsUseCase {
    suspend fun fetchPostSetupQuickActions(): Flow<RestClientResult<ApiResponseWrapper<PostSetupQuickActionList>>>
}