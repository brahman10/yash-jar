package com.jar.app.feature_settings.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_settings.domain.model.LanguageList
import kotlinx.coroutines.flow.Flow

interface FetchSupportedAppLanguagesUseCase {

    suspend fun fetchSupportedLanguages(): Flow<RestClientResult<ApiResponseWrapper<LanguageList>>>

}