package com.jar.app.feature_user_api.domain.use_case

import com.jar.app.feature_user_api.domain.model.AutopayResetRequiredResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface IsAutoInvestResetRequiredUseCase {

    suspend fun isAutoInvestResetRequired(newAmount: Float,savingsType: String): Flow<RestClientResult<ApiResponseWrapper<AutopayResetRequiredResponse>>>
}