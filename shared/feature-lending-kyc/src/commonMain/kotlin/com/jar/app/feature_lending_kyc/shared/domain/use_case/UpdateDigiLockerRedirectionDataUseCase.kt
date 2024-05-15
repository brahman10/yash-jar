package com.jar.app.feature_lending_kyc.shared.domain.use_case

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.DigiLockerVerificationStatus
import com.jar.app.feature_lending_kyc.shared.domain.model.DigilockerRedirectionData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UpdateDigiLockerRedirectionDataUseCase {

    suspend fun updateDigiLockerRedirection(kycFeatureFlowType: KycFeatureFlowType, redirectionData: DigilockerRedirectionData): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

}