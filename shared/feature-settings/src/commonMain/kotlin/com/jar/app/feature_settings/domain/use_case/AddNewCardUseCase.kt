package com.jar.app.feature_settings.domain.use_case


import com.jar.app.feature_settings.domain.model.CardDetail
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface AddNewCardUseCase {
    suspend fun addNewCard(cardDetail: CardDetail): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
}