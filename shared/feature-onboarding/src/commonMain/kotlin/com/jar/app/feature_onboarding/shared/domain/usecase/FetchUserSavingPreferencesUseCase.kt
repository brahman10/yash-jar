package com.jar.app.feature_onboarding.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchUserSavingPreferencesUseCase {

    suspend fun getUserSavingPreferences(): Flow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_onboarding.shared.domain.model.UserSavingPreferences?>>>

}