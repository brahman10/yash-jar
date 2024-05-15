package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyPanDetailsUseCase

internal class VerifyPanDetailsUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
) : VerifyPanDetailsUseCase {
    override suspend fun verifyPanDetails(
        creditReportPAN: CreditReportPAN,
        kycFeatureFlowType: KycFeatureFlowType
    ) =
        lendingKycRepository.verifyPanDetails(creditReportPAN, kycFeatureFlowType)
}