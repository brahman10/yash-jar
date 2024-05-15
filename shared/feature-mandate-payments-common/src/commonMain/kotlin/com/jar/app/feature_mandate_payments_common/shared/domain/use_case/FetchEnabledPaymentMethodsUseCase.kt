package com.jar.app.feature_mandate_payments_common.shared.domain.use_case

import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method.EnabledPaymentMethodResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchEnabledPaymentMethodsUseCase {

    suspend fun fetchEnabledPaymentMethods(flowType: String?): Flow<RestClientResult<ApiResponseWrapper<EnabledPaymentMethodResponse?>>>
}