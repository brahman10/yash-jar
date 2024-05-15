package com.jar.app.feature_gold_redemption.shared.domain.repository


import com.jar.app.feature_gold_redemption.shared.data.network.GoldRedemptionDataSource
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_gold_redemption.shared.data.network.model.request.GoldRedemptionInitiateCreateOrderRequest
import com.jar.app.feature_gold_redemption.shared.data.network.model.AbandonScreenData
import com.jar.app.feature_gold_redemption.shared.data.network.model.MyVouchersAPIResponse
import com.jar.app.feature_gold_redemption.shared.data.network.model.request.FetchVoucherType
import com.jar.app.feature_gold_redemption.shared.data.network.model.MyVouchersTabCountResponse
import com.jar.app.feature_gold_redemption.shared.data.network.model.PendingOrdersAPIResponse
import com.jar.app.feature_gold_redemption.shared.data.network.model.AllCitiesResponse
import com.jar.app.feature_gold_redemption.shared.data.network.model.GoldRedemptionTransactionData
import com.jar.app.feature_gold_redemption.shared.data.network.model.StateData
import com.jar.app.feature_gold_redemption.shared.data.network.model.ViewVoucherDetailsAPIResponse
import kotlinx.coroutines.flow.Flow

internal class GoldRedemptionRepositoryImpl constructor(private val goldRedemptionDataSource: GoldRedemptionDataSource) :
    GoldRedemptionRepository {
    override suspend fun fetchGoldRedemptionIntro() =
        getFlowResult { goldRedemptionDataSource.fetchGoldRedemptionIntro() }

    override suspend fun fetchGoldRedemptionIntroPart2() =
        getFlowResult { goldRedemptionDataSource.fetchGoldRedemptionIntroPart2() }

    override suspend fun fetchFaqs() = getFlowResult { goldRedemptionDataSource.fetchFaqs() }

    override suspend fun fetchBrandCatalogoueStatic() =
        getFlowResult { goldRedemptionDataSource.fetchBrandCatalogoueStatic() }

    override suspend fun fetchAllVouchers(category: String?) =
        getFlowResult { goldRedemptionDataSource.fetchAllVouchers(category) }

    override suspend fun fetchVoucherDetail(string: String) =
        getFlowResult { goldRedemptionDataSource.fetchVoucherDetail(string) }

    override suspend fun initiateOrder(string: GoldRedemptionInitiateCreateOrderRequest) =
        getFlowResult { goldRedemptionDataSource.initiateOrder(string) }

    override suspend fun initiatePayment(
        tnxAmt: String,
        orderId: String
    ): Flow<RestClientResult<ApiResponseWrapper<FetchManualPaymentRequest?>>> =
        getFlowResult { goldRedemptionDataSource.initiatePayment(tnxAmt, orderId) }

    override suspend fun fetchTxnDetailsPolling(
        orderId: String?,
        voucherId: String?,
        showLoading: () -> Unit,
        shouldRetry: (result: RestClientResult<ApiResponseWrapper<GoldRedemptionTransactionData?>>) -> Boolean
    ) = getFlowResult {
        goldRedemptionDataSource.fetchTxnDetailsPolling(orderId, voucherId, showLoading, shouldRetry)
    }

    override suspend fun fetchPurchaseHistory(
    ) = getFlowResult {
        goldRedemptionDataSource.fetchPurchaseHistory()
    }

    override suspend fun fetchviewDetails(voucherId: String, orderId: String): Flow<RestClientResult<ApiResponseWrapper<ViewVoucherDetailsAPIResponse?>>> =
        getFlowResult { goldRedemptionDataSource.fetchViewDetails(voucherId, orderId) }

    override suspend fun fetchabandonScreen(): Flow<RestClientResult<ApiResponseWrapper<AbandonScreenData?>>> =
        getFlowResult { goldRedemptionDataSource.fetchabandonScreen() }

    override suspend fun fetchAllStatesList(brandName: String): Flow<RestClientResult<ApiResponseWrapper<List<StateData?>?>>> {
        return getFlowResult { goldRedemptionDataSource.fetchAllStatesList(brandName) }
    }

    override suspend fun fetchAllCityList(
        stateName: String,
        brandName: String
    ): Flow<RestClientResult<ApiResponseWrapper<AllCitiesResponse?>>> {
        return getFlowResult { goldRedemptionDataSource.fetchAllCityList(stateName, brandName) }
    }

    override suspend fun fetchAllStoreFromCity(
        cityName: String,
        brandName: String
    ): Flow<RestClientResult<ApiResponseWrapper<List<String?>?>>> {
        return getFlowResult { goldRedemptionDataSource.fetchAllStoreFromCity(cityName, brandName) }
    }

    override suspend fun fetchAllMyVouchers(voucherType: FetchVoucherType?): Flow<RestClientResult<ApiResponseWrapper<MyVouchersAPIResponse?>>> =
        getFlowResult { goldRedemptionDataSource.fetchAllMyVouchers(voucherType) }

    override suspend fun fetchUserVouchersCount(): Flow<RestClientResult<ApiResponseWrapper<MyVouchersTabCountResponse?>>> =
        getFlowResult { goldRedemptionDataSource.fetchUserVouchersCount() }

    override suspend fun fetchPendingOrders(): Flow<RestClientResult<ApiResponseWrapper<PendingOrdersAPIResponse?>>> =
        getFlowResult { goldRedemptionDataSource.fetchPendingOrders() }
}