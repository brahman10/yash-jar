package com.jar.app.feature_settings.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_settings.domain.model.SavedCard
import kotlinx.coroutines.flow.Flow

interface FetchUserSavedCardsUseCase {

    suspend fun fetchSavedCards():Flow<RestClientResult<ApiResponseWrapper<List<SavedCard>>>>
}