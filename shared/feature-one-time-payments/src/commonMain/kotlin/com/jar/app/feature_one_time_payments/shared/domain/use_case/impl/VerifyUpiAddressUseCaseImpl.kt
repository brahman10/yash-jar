package com.jar.app.feature_one_time_payments.shared.domain.use_case.impl

import com.jar.app.feature_one_time_payments.shared.data.repository.PaymentRepository
import com.jar.app.feature_one_time_payments.shared.domain.use_case.VerifyUpiAddressUseCase

internal class VerifyUpiAddressUseCaseImpl constructor(
    private val paymentRepository: PaymentRepository
) : VerifyUpiAddressUseCase {

    override suspend fun verifyUpiAddress(
        upiAddress: String,
        isEligibleForMandate: Boolean?
    ) = paymentRepository.verifyUpiAddress(upiAddress,isEligibleForMandate)
}