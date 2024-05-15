package com.jar.app.feature_savings_common.shared.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import kotlinx.coroutines.flow.Flow

interface FetchUserSavingsDetailsUseCase {

    suspend fun fetchSavingsDetails(savingsType: SavingsType): Flow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
}