package com.jar.app.feature.home.domain.usecase

import com.jar.app.feature.home.domain.model.PopupMetaData
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchPopupMetaDataUseCase {
    suspend fun fetchPopupMetaData(): Flow<RestClientResult<PopupMetaData>>
}