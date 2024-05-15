package com.jar.app.feature_profile.domain.use_case

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_profile.domain.model.ProfileStaticData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchDashboardStaticContentUseCase {

    suspend fun fetchDashboardStaticContent(staticContentType: BaseConstants.StaticContentType): Flow<RestClientResult<ApiResponseWrapper<ProfileStaticData>>>

}