package com.jar.app.feature.promo_code.domain.use_case

import com.jar.app.feature.promo_code.domain.data.PromoCode
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

interface FetchPromoCodeUseCase {
    suspend fun fetchPromoCode(
        page: Int,
        size: Int
    ): RestClientResult<ApiResponseWrapper<List<PromoCode>>>
}