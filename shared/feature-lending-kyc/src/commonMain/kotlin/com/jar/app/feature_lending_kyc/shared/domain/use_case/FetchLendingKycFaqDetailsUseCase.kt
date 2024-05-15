package com.jar.app.feature_lending_kyc.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_lending_kyc.shared.domain.model.FaqTypeDetails
import kotlinx.coroutines.flow.Flow

interface FetchLendingKycFaqDetailsUseCase {

    suspend fun fetchLendingKycFaqDetails(param: String): Flow<RestClientResult<ApiResponseWrapper<FaqTypeDetails?>>>
}