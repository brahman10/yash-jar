package com.jar.app.feature_onboarding.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_onboarding.shared.domain.model.LanguageList
import kotlinx.coroutines.flow.Flow

interface FetchSupportedLanguagesUseCase {

    suspend fun fetchSupportedLanguages(): Flow<RestClientResult<ApiResponseWrapper<LanguageList>>>

}