package com.jar.app.feature_lending_kyc.shared.domain.use_case

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.VerifyOtpResponseV2
import com.jar.app.feature_lending_kyc.shared.domain.model.VerifyOtpV2RequestData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface VerifyCreditReportOtpV2UseCase {

    suspend fun verifyCreditReportOtp(requestData: VerifyOtpV2RequestData, kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<VerifyOtpResponseV2?>>>

}