package com.jar.app.feature_gold_redemption.shared.data.repository

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_gold_redemption.shared.data.network.model.ViewVoucherDetailsAPIResponse
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
import com.jar.app.feature_gold_redemption.shared.data.network.model.VoucherPurchaseAPIData
import com.jar.app.feature_gold_redemption.shared.data.network.model.GoldRedemptionTransactionData
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import kotlinx.coroutines.flow.Flow
interface GoldRedemptionRepository : BaseRepository {

    suspend fun fetchGoldRedemptionIntro(): Flow<RestClientResult<ApiResponseWrapper<IntroScrenApiData?>>>
    suspend fun fetchGoldRedemptionIntroPart2(): Flow<RestClientResult<ApiResponseWrapper<IntroScreenAPIDataPart2?>>>
    suspend fun fetchFaqs(): Flow<RestClientResult<ApiResponseWrapper<GenericFAQs?>>>
    suspend fun fetchBrandCatalogoueStatic(): Flow<RestClientResult<ApiResponseWrapper<BrandCatalogoueApiData?>>>
    suspend fun fetchAllVouchers(category: String?): Flow<RestClientResult<ApiResponseWrapper<AllVouchersApiData?>>>
    suspend fun fetchVoucherDetail(string: String): Flow<RestClientResult<ApiResponseWrapper<VoucherPurchaseAPIData?>>>
    suspend fun initiateOrder(goldRedemptionInitiateCreateOrderRequest: GoldRedemptionInitiateCreateOrderRequest): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
    suspend fun fetchAllMyVouchers(voucherType: FetchVoucherType?): Flow<RestClientResult<ApiResponseWrapper<MyVouchersAPIResponse?>>>
    suspend fun fetchUserVouchersCount(): Flow<RestClientResult<ApiResponseWrapper<MyVouchersTabCountResponse?>>>
    suspend fun initiatePayment(
        tnxAmt: String,
        orderId: String
    ): Flow<RestClientResult<ApiResponseWrapper<FetchManualPaymentRequest?>>>


    suspend fun fetchviewDetails(
        voucherId: String,
        orderId: String,
    ): Flow<RestClientResult<ApiResponseWrapper<ViewVoucherDetailsAPIResponse?>>>

    suspend fun fetchabandonScreen(): Flow<RestClientResult<ApiResponseWrapper<AbandonScreenData?>>>
    suspend fun fetchAllStatesList(brandName: String): Flow<RestClientResult<ApiResponseWrapper<List<StateData?>?>>>

    suspend fun fetchAllCityList(
        stateName: String,
        brandName: String,
    ): Flow<RestClientResult<ApiResponseWrapper<AllCitiesResponse?>>>

    suspend fun fetchAllStoreFromCity(
        cityName: String,
        brandName: String,
    ): Flow<RestClientResult<ApiResponseWrapper<List<String?>?>>>

    suspend fun fetchTxnDetailsPolling(
        orderId: String?,
        voucherId: String?,
        showLoading: () -> Unit,
        shouldRetry: (result: RestClientResult<ApiResponseWrapper<GoldRedemptionTransactionData?>>) -> Boolean
    ): Flow<RestClientResult<ApiResponseWrapper<GoldRedemptionTransactionData?>>>

    suspend fun fetchPurchaseHistory(): Flow<RestClientResult<ApiResponseWrapper<VoucherPurchaseApiResponse?>>>
    suspend fun fetchPendingOrders(): Flow<RestClientResult<ApiResponseWrapper<PendingOrdersAPIResponse?>>>
}
