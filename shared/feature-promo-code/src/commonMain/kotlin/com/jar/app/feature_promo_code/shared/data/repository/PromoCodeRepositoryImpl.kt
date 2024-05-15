package com.jar.app.feature_promo_code.shared.data.repository

import com.jar.app.feature_promo_code.shared.data.models.PromoCodeSubmitResponse
import com.jar.app.feature_promo_code.shared.data.models.PromoCodeTransactionResponse
import com.jar.app.feature_promo_code.shared.data.network.PromoCodeDataSource
import com.jar.app.feature_promo_code.shared.domain.repository.PromoCodeRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal class PromoCodeRepositoryImpl constructor(
    private val promoCodeDataSource: PromoCodeDataSource
): PromoCodeRepository {
    override suspend fun applyPromoCode(promoCode: String): Flow<RestClientResult<ApiResponseWrapper<PromoCodeSubmitResponse?>>> = getFlowResult {
        promoCodeDataSource.submitPromoCode(promoCode)
    }

    override suspend fun getPromoCodeTransactionStatus(orderId: String): Flow<RestClientResult<ApiResponseWrapper<PromoCodeTransactionResponse?>>> =
        getFlowResult {
            promoCodeDataSource.getPromoCodeTransactionStatus(orderId)
        }
}