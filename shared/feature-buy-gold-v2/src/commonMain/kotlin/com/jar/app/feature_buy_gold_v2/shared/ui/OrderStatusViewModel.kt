package com.jar.app.feature_buy_gold_v2.shared.ui

import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.util.DynamicCardUtil
import com.jar.app.feature_one_time_payments.shared.data.model.DynamicCardsOrderType
import com.jar.app.feature_one_time_payments.shared.data.model.base.FetchManualPaymentRequest
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchOrderStatusDynamicCardsUseCase
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_user_api.domain.use_case.FetchUserSettingsUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.jar.internal.library.jar_core_network.api.model.RestClientResult as LibraryRestClientResult
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper as LibraryApiResponseWrapper

class OrderStatusViewModel constructor(
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase,
    private val fetchWeeklyChallengeMetaDataUseCase: FetchWeeklyChallengeMetaDataUseCase,
    private val fetchOrderStatusDynamicCardsUseCase: FetchOrderStatusDynamicCardsUseCase,
    private val fetchUserSettingsUseCase: FetchUserSettingsUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    var fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse? = null

    private val _fetchManualPaymentResponseLiveData =
        MutableSharedFlow<LibraryRestClientResult<LibraryApiResponseWrapper<FetchManualPaymentStatusResponse>>>()
    val fetchManualPaymentResponseLiveData: CFlow<LibraryRestClientResult<LibraryApiResponseWrapper<FetchManualPaymentStatusResponse>>>
        get() = _fetchManualPaymentResponseLiveData.toCommonFlow()

    private val _dynamicCardsLiveData = MutableSharedFlow<MutableList<DynamicCard>>()
    val dynamicCardsLiveData: CFlow<MutableList<DynamicCard>>
        get() = _dynamicCardsLiveData.toCommonFlow()

    private val _weeklyChallengeMetaLiveData =
        MutableStateFlow<LibraryRestClientResult<LibraryApiResponseWrapper<WeeklyChallengeMetaData?>>>(
            RestClientResult.none()
        )
    val weeklyChallengeMetaLiveData: CStateFlow<LibraryRestClientResult<LibraryApiResponseWrapper<WeeklyChallengeMetaData?>>>
        get() = _weeklyChallengeMetaLiveData.toCommonStateFlow()

    fun fetchManualPaymentStatus(
        transactionId: String,
        paymentProvider: String,
        flowContext: String
    ) {
        viewModelScope.launch {
            fetchManualPaymentStatusUseCase.fetchManualPaymentStatus(
                FetchManualPaymentRequest(
                    orderId = transactionId,
                    paymentProvider = paymentProvider,
                    flowContext = flowContext
                )
            ).collect {
                _fetchManualPaymentResponseLiveData.emit(it)
            }
        }
    }

    fun fetchOrderStatusDynamicCards(orderId: String) {
        viewModelScope.launch {
            fetchOrderStatusDynamicCardsUseCase.fetchOrderStatusDynamicCards(
                DynamicCardsOrderType.BUY_GOLD,
                orderId
            ).collectUnwrapped(
                onSuccess = {
                    createDynamicCards(it)
                },
                onError = { _, _ ->
                    _dynamicCardsLiveData.emit(mutableListOf())
                }
            )
        }
    }

    private suspend fun createDynamicCards(result: LibraryApiResponseWrapper<Unit?>) {
        val list = mutableListOf<DynamicCard>()
        val views: List<LibraryCardData?>? = result.getViewData()
        for (view: LibraryCardData? in views.orEmpty()) {
            view?.let {
                if (it.showCard) list.add(it)
            }
        }
        DynamicCardUtil.rearrangeDynamicCards(list)
        _dynamicCardsLiveData.emit(list)
    }

    fun fetchWeeklyChallengeMetaData() {
        viewModelScope.launch {
            fetchWeeklyChallengeMetaDataUseCase.fetchWeeklyChallengeMetaData(false).collect {
                _weeklyChallengeMetaLiveData.emit(it)
            }
        }
    }
}