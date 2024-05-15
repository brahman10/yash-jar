package com.jar.app.feature_one_time_payments.shared.domain.use_case.impl

import com.jar.app.feature_one_time_payments.shared.data.repository.PaymentRepository
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchSavedUpiIdUseCase

internal class FetchSavedUpiIdUseCaseImpl constructor(
    private val paymentRepository: PaymentRepository
) : FetchSavedUpiIdUseCase {

    override suspend fun fetchSavedUpiIds() = paymentRepository.fetchSavedUpiIds()
}