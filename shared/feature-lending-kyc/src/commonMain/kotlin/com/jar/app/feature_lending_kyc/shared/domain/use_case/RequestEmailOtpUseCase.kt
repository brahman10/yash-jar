package com.jar.app.feature_lending_kyc.shared.domain.use_case

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_lending_kyc.shared.domain.model.EmailOtp
import kotlinx.coroutines.flow.Flow

interface RequestEmailOtpUseCase {

    suspend fun requestEmailOtp(email: String, kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<EmailOtp?>>>

}