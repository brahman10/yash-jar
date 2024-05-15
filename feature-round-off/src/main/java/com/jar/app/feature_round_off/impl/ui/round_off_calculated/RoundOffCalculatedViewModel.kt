package com.jar.app.feature_round_off.impl.ui.round_off_calculated

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_round_off.shared.domain.model.InitialRoundOff
import com.jar.app.feature_round_off.shared.domain.model.RoundOffBreakUp
import com.jar.app.feature_round_off.shared.domain.model.RoundOffType
import com.jar.app.feature_round_off.shared.domain.use_case.FetchInitialRoundOffTransactionBreakupUseCase
import com.jar.app.feature_round_off.shared.domain.use_case.FetchInitialRoundOffUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class RoundOffCalculatedViewModel @Inject constructor(
    private val initialRoundOffUseCase: FetchInitialRoundOffUseCase,
    private val fetchInitialRoundOffTransactionBreakupUseCase: FetchInitialRoundOffTransactionBreakupUseCase
) : ViewModel() {

    private val _initialRoundOffLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<InitialRoundOff?>>>()
    val initialRoundOffLiveData: LiveData<RestClientResult<ApiResponseWrapper<InitialRoundOff?>>>
        get() = _initialRoundOffLiveData

    private val _paymentTransactionBreakupLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<RoundOffBreakUp>>>()
    val paymentTransactionBreakupLiveData: LiveData<RestClientResult<ApiResponseWrapper<RoundOffBreakUp>>>
        get() = _paymentTransactionBreakupLiveData

    fun fetchInitialRoundOffsData() {
        viewModelScope.launch {
            initialRoundOffUseCase.initialRoundOffsData(type = RoundOffType.SMS.name).collect {
                _initialRoundOffLiveData.postValue(it)
            }
        }
    }
    fun fetchPaymentTransactionBreakup(orderId: String?, type: String?) {
        viewModelScope.launch {
            fetchInitialRoundOffTransactionBreakupUseCase.fetchInitialRoundOffTransactionBreakup(orderId, type)
                .collect {
                    _paymentTransactionBreakupLiveData.postValue(it)
                }
        }
    }
}