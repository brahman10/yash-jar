package com.jar.app.feature_mandate_payments_common.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiResponse
import kotlinx.coroutines.flow.Flow

interface InitiateMandatePaymentUseCase {

    suspend fun initiateMandatePayment(initiateAutoInvestRequest: InitiateMandatePaymentApiRequest?): Flow<RestClientResult<ApiResponseWrapper<InitiateMandatePaymentApiResponse?>>>
}