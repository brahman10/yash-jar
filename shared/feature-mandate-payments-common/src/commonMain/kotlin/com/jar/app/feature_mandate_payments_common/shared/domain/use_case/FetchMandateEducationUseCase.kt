package com.jar.app.feature_mandate_payments_common.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_mandate_payments_common.shared.domain.model.mandate_help.MandateEducationResp
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import kotlinx.coroutines.flow.Flow

interface FetchMandateEducationUseCase {

    suspend fun fetchMandateEducation(mandateStaticContentType: MandatePaymentCommonConstants.MandateStaticContentType): Flow<RestClientResult<ApiResponseWrapper<MandateEducationResp>>>
}