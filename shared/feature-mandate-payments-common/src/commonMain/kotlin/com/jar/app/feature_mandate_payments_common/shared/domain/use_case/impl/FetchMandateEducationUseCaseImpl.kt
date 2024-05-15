package com.jar.app.feature_mandate_payments_common.shared.domain.use_case.impl

import com.jar.app.feature_mandate_payments_common.shared.data.repository.MandatePaymentRepository
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandateEducationUseCase
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants

internal class FetchMandateEducationUseCaseImpl constructor(private val mandatePaymentRepository: MandatePaymentRepository) :
    FetchMandateEducationUseCase {
    override suspend fun fetchMandateEducation(mandateStaticContentType: MandatePaymentCommonConstants.MandateStaticContentType) =
        mandatePaymentRepository.fetchMandateEducation(mandateStaticContentType)
}