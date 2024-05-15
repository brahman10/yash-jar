package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.app.feature_lending.shared.domain.model.v2.StaticContentResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow
interface FetchStaticContentUseCase {

    suspend fun fetchLendingStaticContent(
        loanId: String?,
        staticContentType: String
    ): Flow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>

}