package com.jar.app.feature_promo_code.shared.domain.use_cases.impl

import com.jar.app.feature_promo_code.shared.data.models.PromoCodeTransactionResponse
import com.jar.app.feature_promo_code.shared.domain.repository.PromoCodeRepository
import com.jar.app.feature_promo_code.shared.domain.use_cases.FetchPromoCodeTransactionStatusUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class FetchPromoCodeTransactionStatusUseCaseImpl constructor(
    private val promoCodeRepository: PromoCodeRepository
) : FetchPromoCodeTransactionStatusUseCase {
    override suspend fun fetchPromoCodeTransactionStatus(orderId: String): Flow<RestClientResult<ApiResponseWrapper<PromoCodeTransactionResponse?>>> =
        promoCodeRepository.getPromoCodeTransactionStatus(orderId)
}