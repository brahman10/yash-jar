package com.jar.app.feature_lending_kyc.shared.domain.use_case

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface VerifySelfieUseCase {

    suspend fun verifySelfie(
        selfie: ByteArray,
        kycFeatureFlowType: KycFeatureFlowType,
        loanApplicationId: String? = null
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

}