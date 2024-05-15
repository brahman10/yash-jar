package com.jar.app.feature_vasooli.impl.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_vasooli.impl.domain.model.VasooliEntryRequest
import com.jar.app.feature_vasooli.impl.domain.model.VasooliEntryResponse
import kotlinx.coroutines.flow.Flow

internal interface PostVasooliRequestUseCase {

    suspend fun postVasooliRequest(vasooliEntryRequest: VasooliEntryRequest): Flow<RestClientResult<ApiResponseWrapper<VasooliEntryResponse>>>

}