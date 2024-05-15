package com.jar.app.feature_one_time_payments.shared.domain.use_case.impl

import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments.shared.data.repository.PaymentRepository
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

internal class FetchManualPaymentStatusUseCaseImpl constructor(
    private val paymentRepository: PaymentRepository
) : FetchManualPaymentStatusUseCase {

    override suspend fun fetchManualPaymentStatus(
        fetchManualPaymentRequest: FetchManualPaymentRequest,
        times: Int,
        showLoading: () -> Unit,
        shouldRetry: (response: RestClientResult<ApiResponseWrapper<FetchManualPaymentStatusResponse>>) -> Boolean
    ) =
        paymentRepository.fetchManualPaymentStatus(
            fetchManualPaymentRequest,
            times,
            showLoading,
            shouldRetry
        )

}