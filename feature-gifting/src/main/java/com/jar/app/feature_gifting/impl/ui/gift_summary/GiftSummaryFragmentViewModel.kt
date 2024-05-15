package com.jar.app.feature_gifting.impl.ui.gift_summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByAmountRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByVolumeRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_gifting.shared.domain.model.SendGiftGoldRequest
import com.jar.app.feature_gifting.shared.domain.use_case.SendGiftUseCase
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_one_time_payments_common.shared.SendGiftGoldResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

@HiltViewModel
internal class GiftSummaryFragmentViewModel @Inject constructor(
    private val buyGoldUseCase: BuyGoldUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val sendGiftUseCase: SendGiftUseCase
) : ViewModel() {

    private val _buyPriceLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>()
    val buyPriceLiveData: LiveData<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>
        get() = _buyPriceLiveData

    private val _buyGoldLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val buyGoldLiveData: LiveData<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _buyGoldLiveData

    private val _sendGoldGiftLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<SendGiftGoldResponse?>>>()
    val sendGoldGiftLiveData: LiveData<RestClientResult<ApiResponseWrapper<SendGiftGoldResponse?>>>
        get() = _sendGoldGiftLiveData

    fun fetchBuyPrice() {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY).collectLatest {
                _buyPriceLiveData.postValue(it)
            }
        }
    }

    fun buyGoldByAmount(buyGoldByAmountRequest: BuyGoldByAmountRequest) {
        viewModelScope.launch {
            buyGoldUseCase.buyGoldByAmount(buyGoldByAmountRequest).collect {
                _buyGoldLiveData.postValue(it)
            }
        }
    }

    fun buyGoldByVolume(buyGoldByVolumeRequest: BuyGoldByVolumeRequest) {
        viewModelScope.launch {
            buyGoldUseCase.buyGoldByVolume(buyGoldByVolumeRequest).collect {
                _buyGoldLiveData.postValue(it)
            }
        }
    }

    fun sendGift(sendGiftGoldRequest: SendGiftGoldRequest) {
        viewModelScope.launch {
            sendGiftUseCase.sendGift(sendGiftGoldRequest).collectLatest {
                _sendGoldGiftLiveData.postValue(it)
            }
        }
    }


}