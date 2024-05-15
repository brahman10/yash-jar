package com.jar.app.feature_promo_code.shared.domain.use_cases.impl

import com.jar.app.feature_promo_code.shared.data.models.PromoCodeSubmitResponse
import com.jar.app.feature_promo_code.shared.domain.repository.PromoCodeRepository
import com.jar.app.feature_promo_code.shared.domain.use_cases.ApplyPromoCodeUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class ApplyPromoCodeUseCaseImpl constructor(
    private val promoCodeRepository: PromoCodeRepository
): ApplyPromoCodeUseCase {
    override suspend fun applyPromoCode(promoCode: String): Flow<RestClientResult<ApiResponseWrapper<PromoCodeSubmitResponse?>>> =
        promoCodeRepository.applyPromoCode(promoCode)
}