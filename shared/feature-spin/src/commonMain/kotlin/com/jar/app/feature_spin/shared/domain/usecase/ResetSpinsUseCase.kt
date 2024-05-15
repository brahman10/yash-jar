package com.jar.app.feature_spin.shared.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

interface ResetSpinsUseCase {
    suspend fun resetSpin(spinId: String): RestClientResult<ApiResponseWrapper<Unit?>>
}