package com.jar.app.feature_post_setup.domain.use_case

import com.jar.app.core_base.domain.model.GenericFaqList
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchPostSetupGenericFaqUseCase {

    suspend fun fetchPostSetupFaq(): Flow<RestClientResult<ApiResponseWrapper<GenericFaqList>>>

}