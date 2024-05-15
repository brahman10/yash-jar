package com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl

import com.jar.app.feature_mandate_payments_common.shared.data.repository.MandatePaymentRepository
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchEnabledPaymentMethodsUseCase

class FetchEnabledPaymentMethodsUseCaseImpl constructor(
    private val mandatePaymentRepository: MandatePaymentRepository
) : FetchEnabledPaymentMethodsUseCase {

    override suspend fun fetchEnabledPaymentMethods(flowType: String?) =
        mandatePaymentRepository.fetchEnabledPaymentMethods(flowType)

}