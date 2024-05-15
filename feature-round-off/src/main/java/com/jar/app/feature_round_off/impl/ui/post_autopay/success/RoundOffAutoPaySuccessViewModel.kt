package com.jar.app.feature_round_off.impl.ui.post_autopay.success

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.util.DynamicCardUtil
import com.jar.app.feature_one_time_payments.shared.data.model.DynamicCardsOrderType
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchOrderStatusDynamicCardsUseCase
import com.jar.app.feature_round_off.shared.domain.use_case.FetchInitialRoundOffUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class RoundOffAutoPaySuccessViewModel @Inject constructor(
    private val fetchOrderStatusDynamicCardsUseCase: FetchOrderStatusDynamicCardsUseCase,
    private val initialRoundOffUseCase: FetchInitialRoundOffUseCase,
) : ViewModel() {

    private val _dynamicCardsLiveData = MutableLiveData<MutableList<DynamicCard>>()
    val dynamicCardsLiveData: LiveData<MutableList<DynamicCard>>
        get() = _dynamicCardsLiveData

    private val _initialRoundOffLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_round_off.shared.domain.model.InitialRoundOff?>>>()
    val initialRoundOffLiveData: LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_round_off.shared.domain.model.InitialRoundOff?>>>
        get() = _initialRoundOffLiveData

    fun fetchOrderStatusDynamicCards() {
        viewModelScope.launch {
            fetchOrderStatusDynamicCardsUseCase.fetchOrderStatusDynamicCards(
                DynamicCardsOrderType.ROUND_OFF_SETUP,
                null
            ).collectUnwrapped(
                onSuccess = {
                    createDynamicCards(it)
                },
                onError = { _, _ ->
                    _dynamicCardsLiveData.postValue(mutableListOf())
                }
            )
        }
    }

    private fun createDynamicCards(result: ApiResponseWrapper<Unit?>) {
        val list = mutableListOf<DynamicCard>()
        val views: List<com.jar.app.core_base.domain.model.card_library.LibraryCardData?>? = result.getViewData()
        for (view: com.jar.app.core_base.domain.model.card_library.LibraryCardData? in views.orEmpty()) {
            view?.let {
                if (it.showCard) list.add(it)
            }
        }
        DynamicCardUtil.rearrangeDynamicCards(list)
        _dynamicCardsLiveData.postValue(list)
    }

    fun fetchInitialRoundOffsData() {
        viewModelScope.launch {
            initialRoundOffUseCase.initialRoundOffsData(type = com.jar.app.feature_round_off.shared.domain.model.RoundOffType.SMS.name).collect {
                _initialRoundOffLiveData.postValue(it)
            }
        }
    }
}