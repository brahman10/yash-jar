package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_lending.shared.domain.model.repayment.RepaymentTransactionDetails
import kotlinx.coroutines.flow.Flow

interface FetchTransactionDetailsUseCase {

    suspend fun getTransactionDetails(paymentId: String): Flow<RestClientResult<ApiResponseWrapper<RepaymentTransactionDetails?>>>

}