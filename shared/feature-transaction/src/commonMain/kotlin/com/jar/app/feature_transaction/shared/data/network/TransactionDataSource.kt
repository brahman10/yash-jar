package com.jar.app.feature_transaction.shared.data.network

import com.jar.app.feature_sell_gold_common.shared.TransactionActionType
import com.jar.app.feature_sell_gold_common.shared.WithdrawalAcceptedResponse
import com.jar.app.feature_transaction.shared.domain.model.new_transaction_details.TransactionDetailsV5Data
import com.jar.app.feature_transaction.shared.util.TransactionConstants.Endpoints
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

internal class TransactionDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchUserGoldDetails() = getResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.UserGoldDetailsRes?>> {
        client.get {
            url(Endpoints.FETCH_USER_GOLD_HOLDINGS)
        }
    }

    suspend fun fetchUserWinningDetails() = getResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.UserWinningDetailsRes>> {
        client.get {
            url(Endpoints.FETCH_USER_WINNING_DETAILS)
        }
    }

    suspend fun fetchFilters() = getResult<ApiResponseWrapper<List<com.jar.app.feature_transaction.shared.domain.model.FilterResponse>>> {
        client.get {
            url(Endpoints.FETCH_TRANSACTION_FILTERS)
        }
    }

    suspend fun fetchInvestedAmountBreakup() =
        getResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.InvestmentBreakDown?>> {
            client.get {
                url(Endpoints.FETCH_INVESTED_VALUE_BREAKDOWN)
            }
        }

    suspend fun fetchTransactionListPaginated(request: com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest) =
        getResult<ApiResponseWrapper<List<com.jar.app.feature_transaction.shared.domain.model.TransactionData>>?> {
            client.post {
                url(Endpoints.FETCH_TRANSACTION_LIST)
                setBody(request)
            }
        }

    suspend fun fetchWinningListPaginated(pageNo: Int, pageSize: Int) =
        getResult<ApiResponseWrapper<List<com.jar.app.feature_transaction.shared.domain.model.WinningData>>> {
            client.get {
                url(Endpoints.FETCH_WINNINGS_LIST)
                parameter("pageNo", pageNo)
                parameter("pageSize", pageSize)
            }
        }

    suspend fun fetchTxnDetails(orderId: String, assetSourceType: String, assetTxnId: String) =
        getResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails>> {
            client.get {
                url(Endpoints.FETCH_TRANSACTION_DETAIL)
                parameter("orderId", orderId)
                parameter("assetSourceType", assetSourceType)
                parameter("assetTxnId", assetTxnId)
            }
        }

    suspend fun fetchNewTxnDetails(orderId: String, assetSourceType: String, assetTxnId: String) =
        getResult<ApiResponseWrapper<TransactionDetailsV5Data?>> {
            client.get {
                url(Endpoints.FETCH_NEW_TRANSACTION_DETAILS)
                parameter("orderId", orderId)
                parameter("assetSourceType", assetSourceType)
                parameter("assetTxnId", assetTxnId)
            }
        }

    suspend fun postTransactionAction(type: TransactionActionType, orderId: String, vpa: String) =
        getResult<ApiResponseWrapper<WithdrawalAcceptedResponse>> {
            client.post {
                url(Endpoints.POST_TRANSACTION_ACTION)
                parameter("type", type)
                parameter("orderId", orderId)
                parameter("vpa", vpa)
            }
        }

    suspend fun investWinningInGold(investWinningInGoldRequest: com.jar.app.feature_transaction.shared.domain.model.InvestWinningInGoldRequest) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.INVEST_WINNINGS_IN_GOLD)
                setBody(investWinningInGoldRequest)
            }
        }

    suspend fun fetchPaymentTransactionBreakup(orderId: String?, type: String?) =
        getResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.PaymentTransactionBreakup>> {
            client.get {
                url(Endpoints.FETCH_PAYMENT_TRANSACTION_BREAKUP)
                if (orderId.isNullOrBlank().not())
                    parameter("orderId", orderId)
                if (type.isNullOrBlank().not())
                    parameter("type", type)
            }
        }

    suspend fun fetchPostSetupTransactionDetails(id: String) =
        getResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails>> {
            client.get {
                url(Endpoints.FETCH_POST_SETUP_TRANSACTION_DETAILS)
                parameter("id", id)
            }
        }

    suspend fun fetchUserWinningsBreakdown() =
        getResult<ApiResponseWrapper<com.jar.app.feature_transaction.shared.domain.model.UserWinningBreakdownModel>> {
            client.get {
                url(Endpoints.FETCH_USER_WINNING_BREAKUP)
            }
        }
}
