package com.jar.app.feature_lending_kyc.shared.domain.use_case.impl

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchEmailDeliveryStatusUseCase

internal class FetchEmailDeliveryStatusUseCaseImpl constructor(
    private val lendingKycRepository: LendingKycRepository
): FetchEmailDeliveryStatusUseCase {

    override suspend fun fetchEmailDeliveryStatus(
        email: String,
        msgId: String,
        kycFeatureFlowType: KycFeatureFlowType
    ) = lendingKycRepository.fetchEmailDeliveryStatus(email, msgId, kycFeatureFlowType)

}