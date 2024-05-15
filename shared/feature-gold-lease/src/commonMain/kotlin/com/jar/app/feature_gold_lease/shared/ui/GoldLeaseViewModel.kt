package com.jar.app.feature_gold_lease.shared.ui

import com.jar.app.core_base.domain.mapper.toGoldBalance
import com.jar.app.core_base.domain.model.GoldBalance
import com.jar.app.core_base.util.addPercentage
import com.jar.app.core_base.util.roundDown
import com.jar.app.core_base.util.roundUp
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByVolumeRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.BuyGoldRequestType
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GoldLeaseViewModel constructor(
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val buyGoldUseCase: BuyGoldUseCase,
    private val fetchUserGoldBalanceUseCase: FetchUserGoldBalanceUseCase,
    coroutineScope: CoroutineScope?
){

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _currentGoldBuyPriceFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>(RestClientResult.none())
    val currentGoldBuyPriceFlow: CStateFlow<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>
        get() = _currentGoldBuyPriceFlow.toCommonStateFlow()

    private val _amountFromVolumeFlow =
        MutableSharedFlow<RestClientResult<Float>>()
    val amountFromVolumeFlow: CFlow<RestClientResult<Float>>
        get() = _amountFromVolumeFlow.toCommonFlow()

    private val _volumeFromAmountFlow =
        MutableSharedFlow<RestClientResult<Float>>()
    val volumeFromAmountFlow: CFlow<RestClientResult<Float>>
        get() = _volumeFromAmountFlow.toCommonFlow()

    private val _buyGoldFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val buyGoldFlow: CFlow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _buyGoldFlow.toCommonFlow()

    private val _goldBalanceFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldBalance?>>>(RestClientResult.none())
    val goldBalanceFlow: CStateFlow<RestClientResult<ApiResponseWrapper<GoldBalance?>>>
        get() = _goldBalanceFlow.toCommonStateFlow()

    var goldBalance: GoldBalance? = null

    private var fetchBuyPriceJob: Job? = null
    private var fetchAmountFromVolumeJob: Job? = null
    private var fetchVolumeFromAmountJob: Job? = null

    var fetchCurrentBuyPriceResponse: FetchCurrentGoldPriceResponse? = null

    var buyAmount = 0.0f
    var buyVolume = 0.0f
    var buyGoldRequestType = BuyGoldRequestType.VOLUME
    var currentGoldPrice = 0f
    var currentGoldTax = 0f

    fun fetchCurrentGoldBuyPrice() {
        fetchBuyPriceJob?.cancel()
        fetchBuyPriceJob = viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY)
                .collectUnwrapped(
                    onLoading = {
                        _currentGoldBuyPriceFlow.emit(RestClientResult.loading())
                    },
                    onSuccess = {
                        fetchCurrentBuyPriceResponse = it.data
                        _currentGoldBuyPriceFlow.emit(RestClientResult.success(it))
                    },
                    onError = { errorMessage, _ ->
                        _currentGoldBuyPriceFlow.emit(RestClientResult.error(errorMessage))
                    }
                )
        }
    }

    fun calculateAmountFromVolume(volume: Float) {
        fetchAmountFromVolumeJob?.cancel()
        fetchAmountFromVolumeJob = viewModelScope.launch {
            buyGoldUseCase.calculateAmountFromVolume(volume, fetchCurrentBuyPriceResponse).collect {
                _amountFromVolumeFlow.emit(it)
            }
        }
    }

    fun calculateVolumeFromAmount(amount: Float) {
        fetchVolumeFromAmountJob?.cancel()
        fetchVolumeFromAmountJob = viewModelScope.launch {
            buyAmount = amount
            buyGoldUseCase.calculateVolumeFromAmount(amount, fetchCurrentBuyPriceResponse).collect {
                _volumeFromAmountFlow.emit(it)
                it.data?.let {
                    buyVolume = it
                }
            }
        }
    }

    fun getAmountForXVolume(volume: Float): Float {
        fetchCurrentBuyPriceResponse?.let {
            val currentPriceWithTax = it.price.addPercentage(it.applicableTax!!).roundUp(2)
            return (volume * currentPriceWithTax).roundDown(4)
        } ?: kotlin.run {
            return 0.0f
        }
    }

    fun buyGoldByVolume(buyGoldByVolumeRequest: BuyGoldByVolumeRequest) {
        viewModelScope.launch {
            buyGoldUseCase.buyGoldByVolume(buyGoldByVolumeRequest).collect {
                _buyGoldFlow.emit(it)
            }
        }
    }

    fun fetchUserGoldBalance() {
        viewModelScope.launch {
            fetchUserGoldBalanceUseCase.fetchUserGoldBalance()
                .mapToDTO {
                    it?.toGoldBalance()
                }
                .collect {
                    goldBalance = it.data?.data
                    _goldBalanceFlow.emit(it)
                }
        }
    }
}