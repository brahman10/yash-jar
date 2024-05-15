package com.jar.app.feature_gold_redemption.shared.data.network

import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionConstants.Endpoints
import com.jar.app.feature_gold_redemption.shared.data.network.model.request.GoldRedemptionInitiateCreateOrderRequest
import com.jar.app.feature_gold_redemption.shared.data.network.model.AbandonScreenData
import com.jar.app.feature_gold_redemption.shared.data.network.model.AllVouchersApiData
import com.jar.app.feature_gold_redemption.shared.data.network.model.BrandCatalogoueApiData
import com.jar.app.feature_gold_redemption.shared.data.network.model.GenericFAQs
import com.jar.app.feature_gold_redemption.shared.data.network.model.IntroScreenAPIDataPart2
import com.jar.app.feature_gold_redemption.shared.data.network.model.IntroScrenApiData
import com.jar.app.feature_gold_redemption.shared.data.network.model.MyVouchersAPIResponse
import com.jar.app.feature_gold_redemption.shared.data.network.model.request.FetchVoucherType
import com.jar.app.feature_gold_redemption.shared.data.network.model.MyVouchersTabCountResponse
import com.jar.app.feature_gold_redemption.shared.data.network.model.PendingOrdersAPIResponse
import com.jar.app.feature_gold_redemption.shared.data.network.model.VoucherPurchaseApiResponse
import com.jar.app.feature_gold_redemption.shared.data.network.model.AllCitiesResponse
import com.jar.app.feature_gold_redemption.shared.data.network.model.StateData
import com.jar.app.feature_gold_redemption.shared.data.network.model.ViewVoucherDetailsAPIResponse
import com.jar.app.feature_gold_redemption.shared.data.network.model.VoucherPurchaseAPIData
import com.jar.app.feature_gold_redemption.shared.data.network.model.GoldRedemptionTransactionData
import com.jar.app.feature_gold_redemption.shared.domain.model.GoldRedemptionManualPaymentStatus
import com.jar.app.feature_gold_redemption.shared.domain.model.RefundStatus
import com.jar.app.feature_gold_redemption.shared.util.curateLoadingStatus
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

internal class GoldRedemptionDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchGoldRedemptionIntro() =
        getResult<ApiResponseWrapper<IntroScrenApiData?>> {
            client.get {
                url(Endpoints.URL_INTRO)
            }
        }

    suspend fun fetchGoldRedemptionIntroPart2() =
        getResult<ApiResponseWrapper<IntroScreenAPIDataPart2?>> {
            client.get {
                url(Endpoints.URL_INTRO_2)
            }
        }

    suspend fun fetchFaqs() = getResult<ApiResponseWrapper<GenericFAQs?>> {
        client.get {
            url(Endpoints.URL_FAQS)
        }
    }

    suspend fun fetchBrandCatalogoueStatic() =
        getResult<ApiResponseWrapper<BrandCatalogoueApiData?>> {
            client.get {
                url(Endpoints.URL_LISTING_SCREEN_STATIC)
            }
        }

    suspend fun fetchAllVouchers(category: String?) =
        getResult<ApiResponseWrapper<AllVouchersApiData?>> {
            client.get {
                url(Endpoints.URL_GET_PRODUCTS)
                parameter("type", category)
            }
        }

    suspend fun fetchVoucherDetail(productId: String) =
        getResult<ApiResponseWrapper<VoucherPurchaseAPIData?>> {
            client.get {
                url(Endpoints.URL_GET_PRODUCT)
                parameter("productId", productId)
            }
        }

    suspend fun initiateOrder(goldRedemptionInitiateCreateOrderRequest: GoldRedemptionInitiateCreateOrderRequest) =
        getResult<ApiResponseWrapper<InitiatePaymentResponse?>> {
            client.post {
                url(Endpoints.URL_INITIATE_ORDER)
                setBody(goldRedemptionInitiateCreateOrderRequest)
            }
        }

    suspend fun fetchAllMyVouchers(type: FetchVoucherType?) =
        getResult<ApiResponseWrapper<MyVouchersAPIResponse?>> {
            client.get {
                url(Endpoints.URL_USER_VOUCHERS)
                parameter("type", type)
            }
        }

    suspend fun fetchViewDetails(voucherId: String?, orderId: String?) =
        getResult<ApiResponseWrapper<ViewVoucherDetailsAPIResponse?>> {
            client.get {
                url(Endpoints.URL_USER_VOUCHER)
                if (voucherId.isNullOrBlank().not())
                    parameter("voucherId", voucherId)
                if (orderId.isNullOrBlank().not())
                    parameter("orderId", orderId)
            }
        }

    suspend fun fetchabandonScreen() = getResult<ApiResponseWrapper<AbandonScreenData?>> {
        client.get {
            url(Endpoints.URL_ABANDON_SCREEN)
        }
    }

    suspend fun initiatePayment(
        tnxAmt: String,
        orderId: String
    ) = getResult<ApiResponseWrapper<FetchManualPaymentRequest?>> {
        client.get {
            url(Endpoints.URL_INITIATE_PAYMENT)
            parameter("tnxAmt", tnxAmt)
            parameter("orderId", orderId)
        }
    }

    suspend fun fetchTxnDetailsPolling(orderId: String?, type: String?,
                                       showLoading: () -> Unit,
                                       shouldRetry: (result: RestClientResult<ApiResponseWrapper<GoldRedemptionTransactionData?>>) -> Boolean) =
        retryIOs(
            times = Int.MAX_VALUE,
            initialDelay = 8000L,
            maxDelay = 20000L,
            block = {
                showLoading()
                getResult<ApiResponseWrapper<GoldRedemptionTransactionData?>> {
                    client.get {
                        url(Endpoints.URL_TRANSACTIONS)
                        if (orderId.isNullOrBlank().not())
                            parameter("orderId", orderId)
                        if (type.isNullOrBlank().not())
                            parameter("type", type)
                    }
                }
            },
            shouldRetry = shouldRetry
        )

    suspend fun fetchPurchaseHistory() =
        getResult<ApiResponseWrapper<VoucherPurchaseApiResponse?>> {
            client.get {
                url(Endpoints.URL_PAYMENT_HISTORY)
            }
        }

    suspend fun fetchAllStatesList(brandName: String) =
        getResult<ApiResponseWrapper<List<StateData?>?>> {
            client.get {
                url(Endpoints.URL_GET_ALL_STATES)
                parameter("brandName", brandName)
            }
        }

    suspend fun fetchAllCityList(stateName: String, brandName: String) =
        getResult<ApiResponseWrapper<AllCitiesResponse?>> {
            client.get {
                url(Endpoints.URL_GET_ALL_CITIES)
                parameter("stateName", stateName)
                parameter("brandName", brandName)
            }
        }

    suspend fun fetchAllStoreFromCity(
        cityName: String,
        brandName: String
    ) = getResult<ApiResponseWrapper<List<String?>?>> {
        client.get {
            url(Endpoints.URL_GET_ALL_STORES)
            parameter("cityName", cityName)
            parameter("brandName", brandName)
        }
    }

    suspend fun fetchUserVouchersCount() =
        getResult<ApiResponseWrapper<MyVouchersTabCountResponse?>> {
            client.get {
                url(Endpoints.URL_GET_USER_VOUCHERS_COUNT)
            }
        }

    suspend fun fetchPendingOrders() =
        retryIOs(
            initialDelay = 10000, // 10 sec
            maxDelay = 20000, // 20 sec
            factor = 2.0,
            block = {
                getResult<ApiResponseWrapper<PendingOrdersAPIResponse?>> {
                    client.get{
                        url(Endpoints.URL_GET_PENDING_ORDERS)
                    }
                }
            },
            shouldRetry = {
                it.status != RestClientResult.Status.SUCCESS && it.data?.data?.list?.isNotEmpty() == true
            }
        )
}