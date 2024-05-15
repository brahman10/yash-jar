package com.jar.app.feature_user_api.domain.use_case

import com.jar.app.feature_user_api.data.dto.UserSettingsDTO
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UpdateUserSettingsUseCase {

    suspend fun updateUserSettings(userSettings: UserSettingsDTO): Flow<RestClientResult<ApiResponseWrapper<UserSettingsDTO>>>
}