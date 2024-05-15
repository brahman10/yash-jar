package com.jar.app.feature_buy_gold_v2.shared.data.network

import com.jar.app.core_base.domain.model.InfoDialogResponse
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousDateList
import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousTimeResponse
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldAbandonResponse
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldBottomSheetV2Data
import com.jar.app.feature_buy_gold_v2.shared.domain.model.ContextBannerResponse
import com.jar.app.feature_buy_gold_v2.shared.domain.model.InitiateBuyGoldRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.model.SuggestedAmountData
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2Constants.Endpoints
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class BuyGoldV2DataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    companion object {
        const val BUY_GOLD_OPTIONS = "BUY_GOLD_OPTIONS"
        const val BUY_GOLD_HELP = "BUY_GOLD_HELP"
        const val BUY_GOLD_BOTTOM_SHEET = "BUY_GOLD_BOTTOM_SHEET"
    }

    suspend fun fetchBuyGoldInfo() =
        getResult<ApiResponseWrapper<InfoDialogResponse>> {
            client.get {
                url(Endpoints.FETCH_BUY_GOLD_STATIC_INFO)
                parameter("contentType", BUY_GOLD_HELP)
            }
        }

    suspend fun fetchAuspiciousDates(page: Int, size: Int) =
        getResult<ApiResponseWrapper<AuspiciousDateList>> {
            client.get {
                url(Endpoints.FETCH_AUSPICIOUS_DATES)
                parameter("page", page)
                parameter("size", size)
            }
        }

    suspend fun fetchIsAuspiciousTime() =
        getResult<ApiResponseWrapper<AuspiciousTimeResponse>> {
            client.get {
                url(Endpoints.FETCH_IS_AUSPICIOUS_TIME)
            }
        }

    suspend fun buyGoldManual(initiateBuyGoldRequest: InitiateBuyGoldRequest) =
        getResult<ApiResponseWrapper<InitiatePaymentResponse?>> {
            client.post {
                url(Endpoints.BUY_GOLD_MANUAL)
                setBody(initiateBuyGoldRequest)
            }
        }

    suspend fun fetchBuyGoldOptions(flowContext: String?, couponCode: String?) =
        getResult<ApiResponseWrapper<SuggestedAmountData?>> {
            client.get {
                url(Endpoints.FETCH_BUY_GOLD_STATIC_INFO)
                parameter("contentType", BUY_GOLD_OPTIONS)
                parameter("context", flowContext)
                parameter("couponCode", couponCode)
            }
        }

    suspend fun fetchBuyGoldBottomSheetV2Data() =
        getResult<ApiResponseWrapper<BuyGoldBottomSheetV2Data>> {
            client.get {
                url(Endpoints.FETCH_BUY_GOLD_STATIC_INFO)
                parameter("contentType", BUY_GOLD_BOTTOM_SHEET)
            }
        }

    suspend fun fetchContextBanner(flowContext: String) =
        getResult<ApiResponseWrapper<ContextBannerResponse?>> {
            client.get {
                url(Endpoints.FETCH_CONTEXT_BANNER)
                parameter("context", flowContext)
            }
        }

    suspend fun fetchBuyGoldAbandonInfo(staticContentType: BaseConstants.StaticContentType) =
        getResult<ApiResponseWrapper<BuyGoldAbandonResponse>> {
            client.get {
                url(Endpoints.FETCH_BUY_GOLD_STATIC_INFO)
                parameter("contentType", staticContentType.name)
            }
        }
}