package com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl

import com.jar.app.feature_mandate_payments_common.shared.data.repository.MandatePaymentRepository
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.InitiateMandatePaymentUseCase

internal class InitiateMandatePaymentUseCaseImpl constructor(
    private val mandatePaymentRepository: MandatePaymentRepository
) : InitiateMandatePaymentUseCase {

    override suspend fun initiateMandatePayment(initiateAutoInvestRequest: InitiateMandatePaymentApiRequest?) =
        mandatePaymentRepository.initiateMandatePayment(initiateAutoInvestRequest)
}