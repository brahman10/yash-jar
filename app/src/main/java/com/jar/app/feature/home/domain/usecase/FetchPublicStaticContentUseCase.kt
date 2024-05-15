package com.jar.app.feature.home.domain.usecase

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature.home.domain.model.DashboardStaticData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchPublicStaticContentUseCase {

    suspend fun fetchPublicStaticContent(
        staticContentType: BaseConstants.StaticContentType,
        phoneNumber: String,
        context: String?
    ): Flow<RestClientResult<ApiResponseWrapper<DashboardStaticData?>>>
}