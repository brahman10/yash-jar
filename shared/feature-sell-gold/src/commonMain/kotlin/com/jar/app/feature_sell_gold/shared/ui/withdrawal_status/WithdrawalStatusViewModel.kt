package com.jar.app.feature_sell_gold.shared.ui.withdrawal_status

import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.DynamicCardUtil
import com.jar.app.feature_one_time_payments.shared.data.model.DynamicCardsOrderType
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchOrderStatusDynamicCardsUseCase
import com.jar.app.feature_sell_gold.shared.domain.models.RetryPayoutResponse
import com.jar.app.feature_sell_gold.shared.domain.models.SellGoldStaticData
import com.jar.app.feature_sell_gold.shared.domain.models.WithdrawRequest
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchSellGoldStaticContentUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchWithdrawalStatusUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IPostTransactionActionUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IPostWithdrawRequestUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IUpdateWithdrawalReasonUseCase
import com.jar.app.feature_sell_gold_common.shared.TransactionActionType
import com.jar.app.feature_sell_gold_common.shared.WithdrawalAcceptedResponse
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WithdrawalStatusViewModel(
    private val postWithdrawRequestUseCase: IPostWithdrawRequestUseCase,
    private val fetchWithdrawalStatusUseCase: IFetchWithdrawalStatusUseCase,
    private val updateWithdrawalReasonUseCase: IUpdateWithdrawalReasonUseCase,
    private val fetchSellGoldStaticContentUseCase: IFetchSellGoldStaticContentUseCase,
    private val postTransactionActionUseCase: IPostTransactionActionUseCase,
    private val fetchOrderStatusDynamicCardsUseCase: FetchOrderStatusDynamicCardsUseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main.immediate)

    private val _postWithdrawalRequestLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<WithdrawalAcceptedResponse?>>>(RestClientResult.none())
    val postWithdrawalRequestLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<WithdrawalAcceptedResponse?>>>
        get() = _postWithdrawalRequestLiveData.toCommonStateFlow()

    private val _retryWithdrawalRequestLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<RetryPayoutResponse?>>>(RestClientResult.none())
    val retryWithdrawalRequestLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<RetryPayoutResponse?>>>
        get() = _retryWithdrawalRequestLiveData.toCommonStateFlow()

    private val _fetchWithdrawalStatusLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<WithdrawalAcceptedResponse?>>>(RestClientResult.none())
    val fetchWithdrawalStatusLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<WithdrawalAcceptedResponse?>>>
        get() = _fetchWithdrawalStatusLiveData.toCommonStateFlow()

    private val _updateWithdrawalReasonLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val updateWithdrawalReasonLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _updateWithdrawalReasonLiveData.toCommonStateFlow()

    private val _withdrawalReasonsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<SellGoldStaticData?>>>(RestClientResult.none())
    val withdrawalReasonsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<SellGoldStaticData?>>>
        get() = _withdrawalReasonsLiveData.toCommonStateFlow()

    private val _dynamicCardsLiveData = MutableStateFlow<MutableList<DynamicCard>>(mutableListOf())
    val dynamicCardsLiveData: CStateFlow<MutableList<DynamicCard>>
        get() = _dynamicCardsLiveData.toCommonStateFlow()

    fun postWithdrawalRequest(withdrawRequest: WithdrawRequest) {
        viewModelScope.launch {
            postWithdrawRequestUseCase.invoke(withdrawRequest).collect {
                _postWithdrawalRequestLiveData.value = it
            }
        }
    }

    fun fetchWithdrawalStatus(orderId: String) {
        viewModelScope.launch {
            fetchWithdrawalStatusUseCase.fetchWithdrawalStatus(orderId).collect {
                _fetchWithdrawalStatusLiveData.value = it
            }
        }
    }

    fun fetchWithdrawalReasons() {
        viewModelScope.launch {
            fetchSellGoldStaticContentUseCase.fetchDashboardStaticContent(BaseConstants.StaticContentType.WITHDRAW_REASONS_V2)
                .collect {
                    _withdrawalReasonsLiveData.value = it
                }
        }
    }

    fun updateWithdrawalReason(reason: String) {
        viewModelScope.launch {
            updateWithdrawalReasonUseCase.updateWithdrawalReason(
                postWithdrawalRequestLiveData.value?.data?.data?.orderId.orEmpty(), reason
            ).collect {
                _updateWithdrawalReasonLiveData.value = it
            }
        }
    }

    fun retryWithdrawal(orderId: String, vpa: String) {
        viewModelScope.launch {
            postTransactionActionUseCase.postTransactionAction(
                type = TransactionActionType.RETRY_PAYOUT_VPA, orderId = orderId, vpa = vpa
            ).collect {
                _retryWithdrawalRequestLiveData.value = it
            }
        }
    }

    fun fetchOrderStatusDynamicCards() {
        viewModelScope.launch {
            fetchOrderStatusDynamicCardsUseCase.fetchOrderStatusDynamicCards(
                DynamicCardsOrderType.SELL_GOLD, null
            ).collectUnwrapped(
                onSuccess = {
                    createDynamicCards(it)
                }, onError = { _, _ ->
                    _dynamicCardsLiveData.value = mutableListOf()
                })
        }
    }

    private fun createDynamicCards(result: ApiResponseWrapper<Unit?>) {
        val list = mutableListOf<DynamicCard>()
        val views: List<LibraryCardData?>? = result.getViewData()
        views?.forEach { view ->
            view?.let {
                if (it.showCard) list.add(it)
            }
        }
        DynamicCardUtil.rearrangeDynamicCards(list)
        _dynamicCardsLiveData.value = list
    }
}