package com.jar.app.feature_onboarding.shared.domain.usecase

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_onboarding.shared.domain.model.FaqStaticData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchFaqStaticDataUseCase {

    suspend fun fetchFaqStaticData(staticContentType: BaseConstants.StaticContentType): Flow<RestClientResult<ApiResponseWrapper<FaqStaticData>>>

}