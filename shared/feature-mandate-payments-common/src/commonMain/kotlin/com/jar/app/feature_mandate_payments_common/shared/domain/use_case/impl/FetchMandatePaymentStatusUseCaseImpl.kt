package com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl

import com.jar.app.feature_mandate_payments_common.shared.data.repository.MandatePaymentRepository
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandatePaymentStatusUseCase

internal class FetchMandatePaymentStatusUseCaseImpl constructor(
    private val mandatePaymentRepository: MandatePaymentRepository
) : FetchMandatePaymentStatusUseCase {

    override suspend fun fetchMandatePaymentStatus(mandatePaymentResultFromSDK: MandatePaymentResultFromSDK) =
        mandatePaymentRepository.fetchMandatePaymentStatus(mandatePaymentResultFromSDK)
}