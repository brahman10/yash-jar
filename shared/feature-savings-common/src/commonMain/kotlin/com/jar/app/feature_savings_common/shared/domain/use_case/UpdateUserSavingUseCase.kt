package com.jar.app.feature_savings_common.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_savings_common.shared.domain.model.UpdateUserSavingRequest
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import kotlinx.coroutines.flow.Flow

interface UpdateUserSavingUseCase {
    suspend fun updateUserSavings(updateUserSavingRequest: UpdateUserSavingRequest): Flow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
}