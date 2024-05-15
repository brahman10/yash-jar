package com.jar.app.feature_promo_code.shared.domain.repository

import com.jar.app.feature_promo_code.shared.data.models.PromoCodeSubmitResponse
import com.jar.app.feature_promo_code.shared.data.models.PromoCodeTransactionResponse
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface PromoCodeRepository : BaseRepository {
    suspend fun applyPromoCode(promoCode: String): Flow<RestClientResult<ApiResponseWrapper<PromoCodeSubmitResponse?>>>
    suspend fun getPromoCodeTransactionStatus(orderId: String): Flow<RestClientResult<ApiResponseWrapper<PromoCodeTransactionResponse?>>>

}