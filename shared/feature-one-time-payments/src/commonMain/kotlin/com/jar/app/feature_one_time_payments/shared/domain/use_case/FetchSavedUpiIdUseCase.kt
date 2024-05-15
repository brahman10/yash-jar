package com.jar.app.feature_one_time_payments.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_one_time_payments.shared.domain.model.SavedUpiIdsResponse
import kotlinx.coroutines.flow.Flow

interface FetchSavedUpiIdUseCase {
    suspend fun fetchSavedUpiIds(): Flow<RestClientResult<ApiResponseWrapper<SavedUpiIdsResponse>>>
}