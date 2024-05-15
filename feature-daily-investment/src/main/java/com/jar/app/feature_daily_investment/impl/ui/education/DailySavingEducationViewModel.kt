package com.jar.app.feature_daily_investment.impl.ui.education

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.core_base.util.orFalse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.util.DynamicCardUtil
import com.jar.app.feature_daily_investment.shared.domain.model.DailySavingEducation
import com.jar.app.feature_daily_investment.shared.domain.model.DailySavingEducationResp
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDSEducationUseCase
import com.jar.app.feature_one_time_payments.shared.data.model.DynamicCardsOrderType
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchOrderStatusDynamicCardsUseCase
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DailySavingEducationViewModel @Inject constructor(
    private val fetchDSEducationUseCase: FetchDSEducationUseCase,
    private val fetchOrderStatusDynamicCardsUseCase: FetchOrderStatusDynamicCardsUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _educationSavingEducationLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<DailySavingEducationResp>>>()
    val educationSavingEducationLiveData: LiveData<RestClientResult<ApiResponseWrapper<DailySavingEducationResp>>>
        get() = _educationSavingEducationLiveData

    private val _educationListLiveData =
        MutableLiveData<RestClientResult<List<DailySavingEducation>>>()
    val educationListLiveData: LiveData<RestClientResult<List<DailySavingEducation>>>
        get() = _educationListLiveData

    private val _dynamicCardsLiveData = MutableLiveData<MutableList<DynamicCard>>()
    val dynamicCardsLiveData: LiveData<MutableList<DynamicCard>>
        get() = _dynamicCardsLiveData

    val isListExhaustedLiveData = MutableLiveData(false)
    val dataList = ArrayList<DailySavingEducation>()
    var dataListSize = 0

    fun fetchEducationList() {
        viewModelScope.launch {
            fetchDSEducationUseCase.fetchDSEducationData().collect {
                it.data?.data?.dailySavingEducationData?.dailySavingEducation?.let {
                    dataListSize = it.size
                    dataList.addAll(it)
                }
                _educationSavingEducationLiveData.postValue(it)
            }
        }
    }

    fun emitDsEducationData(position: Int) {
        viewModelScope.launch(dispatcherProvider.default) {
            if (position < dataListSize) {
                val tempList = ArrayList<DailySavingEducation>()
                dataList.mapIndexed { pos, data ->
                    if (pos <= position)
                        tempList.add(data.copy(isExpanded = pos == position))
                }
                _educationListLiveData.postValue(RestClientResult.success(tempList))
            } else {
                if (isListExhaustedLiveData.value.orFalse().not()) {
                    val tempList =
                        dataList.map { it.copy(isExpanded = false, isListIterated = true) }
                    _educationListLiveData.postValue(RestClientResult.success(tempList))
                    isListExhaustedLiveData.postValue(true)
                }
            }
        }
    }

    fun fetchOrderStatusDynamicCards() {
        viewModelScope.launch {
            fetchOrderStatusDynamicCardsUseCase.fetchOrderStatusDynamicCards(
                DynamicCardsOrderType.DAILY_SAVINGS_SETUP,
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
        views?.forEach {
            it?.let {
                if (it.showCard) list.add(it)
            }
        }
        DynamicCardUtil.rearrangeDynamicCards(list)
        _dynamicCardsLiveData.postValue(list)
    }
}