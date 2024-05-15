package com.jar.app.feature_mandate_payments_common.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import kotlinx.coroutines.flow.Flow

interface FetchMandatePaymentStatusUseCase {
    suspend fun fetchMandatePaymentStatus(mandatePaymentResultFromSDK: MandatePaymentResultFromSDK): Flow<RestClientResult<ApiResponseWrapper<FetchMandatePaymentStatusResponse?>>>
}