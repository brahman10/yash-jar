package com.jar.app.feature_promo_code.shared.domain.use_cases

import com.jar.app.feature_promo_code.shared.data.models.PromoCodeSubmitResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface ApplyPromoCodeUseCase {
    suspend fun applyPromoCode(promoCode: String): Flow<RestClientResult<ApiResponseWrapper<PromoCodeSubmitResponse?>>>
}