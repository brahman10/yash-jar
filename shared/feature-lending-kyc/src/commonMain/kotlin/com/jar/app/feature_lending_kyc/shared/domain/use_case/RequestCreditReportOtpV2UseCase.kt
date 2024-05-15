package com.jar.app.feature_lending_kyc.shared.domain.use_case

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportOtpResponseV2
import kotlinx.coroutines.flow.Flow

interface RequestCreditReportOtpV2UseCase {

    suspend fun requestCreditReportOtp(kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<CreditReportOtpResponseV2?>>>

}