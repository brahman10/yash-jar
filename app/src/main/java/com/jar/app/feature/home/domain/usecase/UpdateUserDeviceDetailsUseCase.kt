package com.jar.app.feature.home.domain.usecase

import com.jar.app.feature.home.domain.model.UserDeviceDetails
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface UpdateUserDeviceDetailsUseCase {

    suspend fun updateUserDeviceDetails(userDeviceDetails: UserDeviceDetails): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

}