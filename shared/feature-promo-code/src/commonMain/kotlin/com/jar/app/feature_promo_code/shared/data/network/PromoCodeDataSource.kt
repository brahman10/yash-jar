package com.jar.app.feature_promo_code.shared.data.network

import com.jar.app.feature_promo_code.shared.data.models.PromoCodeSubmitRequest
import com.jar.app.feature_promo_code.shared.data.models.PromoCodeSubmitResponse
import com.jar.app.feature_promo_code.shared.data.models.PromoCodeTransactionResponse
import com.jar.app.feature_promo_code.shared.util.Constants
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

internal class PromoCodeDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun submitPromoCode(promoCode:String) =
        getResult<ApiResponseWrapper<PromoCodeSubmitResponse?>> {
            client.post {
                url(Constants.Endpoints.SUBMIT_PROMOCODE)
               setBody(PromoCodeSubmitRequest(promoCode))

            }
        }

    suspend fun getPromoCodeTransactionStatus(orderId: String)=
        getResult<ApiResponseWrapper<PromoCodeTransactionResponse?>> {
            client.get {
                url(Constants.Endpoints.PROMO_CODE_TRANSACTION_STATUS)
                parameter("orderId", orderId)
            }
        }

}