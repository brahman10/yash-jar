package com.jar.app.feature.home.domain.usecase

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.home.domain.model.ApplyPromoResponse
import com.jar.app.feature_user_api.domain.model.DeviceDetails
import kotlinx.coroutines.flow.Flow

interface ApplyPromoCodeUseCase {

    suspend fun applyPromoCode(
        deviceDetails: DeviceDetails,
        promoCode: String,
        id: String? = null,
        type: String? = null
    ): Flow<RestClientResult<ApiResponseWrapper<ApplyPromoResponse>>>

}