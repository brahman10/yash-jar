package com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl

import com.jar.app.feature_mandate_payments_common.shared.data.repository.MandatePaymentRepository
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchPreferredBankUseCase

internal class FetchPreferredBankUseCaseImpl constructor(
    private val mandatePaymentRepository: MandatePaymentRepository
): FetchPreferredBankUseCase {
    override suspend fun fetchPreferredBank() = mandatePaymentRepository.fetchPreferredBank()
}