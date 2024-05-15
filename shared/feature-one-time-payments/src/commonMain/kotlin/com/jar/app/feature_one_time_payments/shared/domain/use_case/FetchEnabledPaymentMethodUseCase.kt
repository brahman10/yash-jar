package com.jar.app.feature_one_time_payments.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_one_time_payments.shared.domain.model.EnabledPaymentMethodResponse
import kotlinx.coroutines.flow.Flow

interface FetchEnabledPaymentMethodUseCase {

    suspend fun fetchEnabledPaymentMethods(transactionType: String?): Flow<RestClientResult<ApiResponseWrapper<EnabledPaymentMethodResponse>>>

}