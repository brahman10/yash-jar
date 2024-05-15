package com.jar.app.feature_sell_gold.shared.data.network

import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.feature_sell_gold.shared.domain.models.DrawerDetailsResponse
import com.jar.app.feature_sell_gold.shared.domain.models.GoldSellOptionResponse
import com.jar.app.feature_sell_gold.shared.domain.models.KycDetailsResponse
import com.jar.app.feature_sell_gold.shared.domain.models.RetryPayoutResponse
import com.jar.app.feature_sell_gold.shared.domain.models.SellGoldStaticData
import com.jar.app.feature_sell_gold.shared.domain.models.WithdrawHelpData
import com.jar.app.feature_sell_gold.shared.domain.models.WithdrawRequest
import com.jar.app.feature_sell_gold.shared.utils.SellGoldConstants.Endpoints
import com.jar.app.feature_sell_gold_common.shared.TransactionActionType
import com.jar.app.feature_sell_gold_common.shared.WithdrawalAcceptedResponse
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

internal class WithdrawalDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun makeWithdrawalRequest(request: WithdrawRequest) =
        getResult<ApiResponseWrapper<WithdrawalAcceptedResponse?>> {
            client.post {
                url(Endpoints.POST_WITHDRAWAL_REQUEST)
                setBody(request)
            }
        }

    suspend fun fetchGoldSellOptions() =
        getResult<ApiResponseWrapper<GoldSellOptionResponse?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_SELL_OPTIONS)
            }
        }

    suspend fun fetchDashboardStaticContent(staticContentType: StaticContentType) =
        getResult<ApiResponseWrapper<SellGoldStaticData?>> {
            client.get {
                url(Endpoints.FETCH_STATIC_CONTENT)
                parameter("contentType", staticContentType.name)
            }
        }

    suspend fun fetchWithdrawalStatus(orderId: String) =
        getResult<ApiResponseWrapper<WithdrawalAcceptedResponse?>> {
            client.get {
                url(Endpoints.FETCH_WITHDRAWAL_STATUS)
                parameter("orderId", orderId)
            }
        }

    suspend fun updateWithdrawalReason(orderId: String, reason: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.get {
                url(Endpoints.POST_WITHDRAWAL_REASON)
                parameter("orderId", orderId)
                parameter("reason", reason)
            }
        }

    suspend fun postTransactionAction(type: TransactionActionType, orderId: String, vpa: String) =
        getResult<ApiResponseWrapper<RetryPayoutResponse?>> {
            client.post {
                url(Endpoints.POST_TRANSACTION_ACTION)
                parameter("type", type.name)
                parameter("orderId", orderId)
                parameter("vpa", vpa)
            }
        }

    suspend fun fetchWithdrawBottomSheetData() =
        getResult<ApiResponseWrapper<WithdrawHelpData?>> {
            client.get {
                url(Endpoints.FETCH_WITHDRAWAL_HELP_DATA)
                parameter("contentType", StaticContentType.WITHDRAW_HELP.name)
            }
        }

    suspend fun fetchDrawerDetails() = getResult<ApiResponseWrapper<DrawerDetailsResponse?>> {
        client.get {
            url(Endpoints.GET_DRAWER_DETAILS)
        }
    }

    suspend fun fetchKycDetails() = getResult<ApiResponseWrapper<KycDetailsResponse?>> {
        client.get {
            url(Endpoints.GET_KYC_ACTION)
        }
    }
}