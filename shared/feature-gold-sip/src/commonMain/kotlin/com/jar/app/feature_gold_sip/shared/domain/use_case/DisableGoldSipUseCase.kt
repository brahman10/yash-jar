package com.jar.app.feature_gold_sip.shared.domain.use_case

import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

 interface DisableGoldSipUseCase {

    suspend fun disableGoldSip(): Flow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>

}