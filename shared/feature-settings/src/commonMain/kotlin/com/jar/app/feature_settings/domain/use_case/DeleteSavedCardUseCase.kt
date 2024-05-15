package com.jar.app.feature_settings.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_settings.domain.model.DeleteCard
import kotlinx.coroutines.flow.Flow

interface DeleteSavedCardUseCase {

    suspend fun deleteSavedCardUseCase(deleteCard: DeleteCard): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}