package com.jar.app.feature_homepage.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.core_base.domain.model.InfoDialogResponse
import kotlinx.coroutines.flow.Flow

interface FetchStaticPopupInfoUseCase {

    suspend fun fetchStaticPopupInfo(contentType: String): Flow<RestClientResult<ApiResponseWrapper<InfoDialogResponse>>>
}