package com.jar.app.feature_lending_kyc.shared.domain.use_case

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.DigiLockerScreenContent
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchDigiLockerScreenContentUseCase {

    suspend fun fetchDigiLockerScreenContent(
        applicationId: String,
        kycFeatureFlowType: KycFeatureFlowType
    ): Flow<RestClientResult<ApiResponseWrapper<DigiLockerScreenContent?>>>

}