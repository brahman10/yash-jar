package com.jar.app.feature_transaction.impl.ui.winning.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper as LibraryApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult as LibraryRestClientResult
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.InvestWinningInGoldUseCase
import com.jar.app.feature_user_api.domain.model.WinningResponse
import com.jar.app.feature_user_api.domain.use_case.FetchUserWinningsUseCase
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class WithdrawWinningFragmentViewModel @Inject constructor(
    private val fetchUserWinningsUseCase: FetchUserWinningsUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val investWinningInGoldUseCase: InvestWinningInGoldUseCase
) : ViewModel() {

    private var fetchCurrentBuyPriceResponse: FetchCurrentGoldPriceResponse? = null

    private val _userWinningsLiveData =
        MutableLiveData<LibraryRestClientResult<LibraryApiResponseWrapper<WinningResponse>>>()
    val userWinningsLiveData: LiveData<LibraryRestClientResult<LibraryApiResponseWrapper<WinningResponse>>>
        get() = _userWinningsLiveData

    private val _currentGoldBuyPriceLiveData =
        MutableLiveData<LibraryRestClientResult<LibraryApiResponseWrapper<FetchCurrentGoldPriceResponse>>>()
    val currentGoldBuyPriceLiveData: LiveData<LibraryRestClientResult<LibraryApiResponseWrapper<FetchCurrentGoldPriceResponse>>>
        get() = _currentGoldBuyPriceLiveData

    private val _investInWinningLiveData =
        MutableLiveData<LibraryRestClientResult<LibraryApiResponseWrapper<Unit?>>>()
    val investInWinningLiveData: LiveData<LibraryRestClientResult<LibraryApiResponseWrapper<Unit?>>>
        get() = _investInWinningLiveData

    fun fetchUserWinnings() {
        viewModelScope.launch {
            fetchUserWinningsUseCase.fetchUserWinnings().collect {
                _userWinningsLiveData.postValue(it)
            }
        }
    }

    fun fetchCurrentGoldBuyPrice() {
        viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY).collectUnwrapped(
                onLoading = {
                    _currentGoldBuyPriceLiveData.postValue(LibraryRestClientResult.loading())
                },
                onSuccess = {
                    fetchCurrentBuyPriceResponse = it.data
                    _currentGoldBuyPriceLiveData.postValue(LibraryRestClientResult.success(it))
                },
                onError = { errorMessage, errorCode ->
                    _currentGoldBuyPriceLiveData.postValue(LibraryRestClientResult.error(errorMessage))
                }
            )
        }
    }

    fun investWinningInGold(amount: Double) {
        viewModelScope.launch {
            if (fetchCurrentBuyPriceResponse != null) {
                investWinningInGoldUseCase.investWinningInGold(
                    com.jar.app.feature_transaction.shared.domain.model.InvestWinningInGoldRequest(
                        amount = amount,
                        priceResponse = fetchCurrentBuyPriceResponse!!
                    )
                ).collect {
                    _investInWinningLiveData.postValue(it)
                }
            }
        }
    }
}