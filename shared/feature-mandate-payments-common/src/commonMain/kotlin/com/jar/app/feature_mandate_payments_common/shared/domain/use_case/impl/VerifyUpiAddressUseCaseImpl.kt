package com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl

import com.jar.app.feature_mandate_payments_common.shared.data.repository.MandatePaymentRepository
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.VerifyUpiAddressUseCase

internal class VerifyUpiAddressUseCaseImpl constructor(
    private val mandatePaymentRepository: MandatePaymentRepository
) : VerifyUpiAddressUseCase {

    override suspend fun verifyUpiAddress(upiAddress: String) =
        mandatePaymentRepository.verifyUpiAddress(upiAddress, true)

}