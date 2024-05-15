package com.jar.app.feature_round_off.impl.ui.initial_round_off

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_round_off.shared.domain.model.RoundOffBreakUp
import com.jar.app.feature_round_off.shared.domain.use_case.FetchInitialRoundOffTransactionBreakupUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class InitialRoundOffViewModel @Inject constructor(
    private val fetchInitialRoundOffTransactionBreakupUseCase: FetchInitialRoundOffTransactionBreakupUseCase
): ViewModel() {
    private val _paymentTransactionBreakupLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_round_off.shared.domain.model.RoundOffBreakUp>>>()
    val paymentTransactionBreakupLiveData: LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_round_off.shared.domain.model.RoundOffBreakUp>>>
        get() = _paymentTransactionBreakupLiveData

    fun fetchPaymentTransactionBreakup(orderId: String?, type: String?) {
        viewModelScope.launch {
            fetchInitialRoundOffTransactionBreakupUseCase.fetchInitialRoundOffTransactionBreakup(orderId, type)
                .collect {
                    _paymentTransactionBreakupLiveData.postValue(it)
                }
        }
    }
}