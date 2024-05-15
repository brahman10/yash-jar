package com.jar.app.feature_lending.shared.domain.use_case

import com.jar.app.feature_lending.shared.domain.model.temp.LendingAddress
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UpdateAddressDetailsUseCase {

    suspend fun updateAddressDetails(lendingAddress: LendingAddress): Flow<RestClientResult<ApiResponseWrapper<LendingAddress?>>>

}