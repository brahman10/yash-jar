package com.jar.app.feature_settings.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_user_api.domain.model.SavedVPA
import kotlinx.coroutines.flow.Flow

interface AddNewUpiIdUseCase {
    suspend fun addNewUpiId(upiId: String): Flow<RestClientResult<ApiResponseWrapper<SavedVPA?>>>
}