package com.jar.app.feature_mandate_payments_common.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_upi.VerifyUpiAddressResponse
import kotlinx.coroutines.flow.Flow

interface VerifyUpiAddressUseCase {
    suspend fun verifyUpiAddress(upiAddress: String): Flow<RestClientResult<ApiResponseWrapper<VerifyUpiAddressResponse>>>
}