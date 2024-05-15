package com.jar.app.feature_homepage.impl.ui.first_gold_coin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_homepage.shared.domain.model.FirstCoinTransitionData
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinOnboardingStatusUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinTransitionUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUpdateFirstCoinOrderIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class FirstCoinTransitionViewModel @Inject constructor(
    private val fetchFirstCoinTransitionUseCase: FetchFirstCoinTransitionUseCase,
    private val fetchUpdateFirstCoinOrderIdUseCase: FetchUpdateFirstCoinOrderIdUseCase,
    private val fetchFirstCoinOnboardingStatusUseCase: FetchFirstCoinOnboardingStatusUseCase
) : ViewModel(
) {
    private val _transitionPageLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_homepage.shared.domain.model.FirstCoinTransitionData>>>()
    val transitionPageLiveData: LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_homepage.shared.domain.model.FirstCoinTransitionData>>>
        get() = _transitionPageLiveData


    fun fetchTransitionPageData() {
        viewModelScope.launch {
            fetchFirstCoinTransitionUseCase.fetchFirstCoinTransitionPageData().collect {
                _transitionPageLiveData.postValue(it)
            }
        }
    }

    fun sendOrderId(orderId: String) {
        viewModelScope.launch {
            fetchUpdateFirstCoinOrderIdUseCase.updateFirstCoinDeliveryStatus(orderId).collect {
            }
        }
    }

    fun sendFirstCoinOnboardingStatus(){
        viewModelScope.launch {
            fetchFirstCoinOnboardingStatusUseCase.sendFirstCoinOnboardingStatus().collect {

            }
        }
    }
}
