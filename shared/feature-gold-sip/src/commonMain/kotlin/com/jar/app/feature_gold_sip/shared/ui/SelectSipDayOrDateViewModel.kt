package com.jar.app.feature_gold_sip.shared.ui

import com.jar.app.feature_gold_sip.shared.domain.model.WeekOrMonthData
import com.jar.app.feature_gold_sip.shared.util.MonthGenerator
import com.jar.app.feature_gold_sip.shared.util.WeekGenerator
import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SelectSipDayOrDateViewModel constructor(
    private val monthGenerator: MonthGenerator,
    private val weekGenerator: WeekGenerator,
    private val updateGoldSipDetailsUseCase: UpdateGoldSipDetailsUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
)  {

    private val viewModelScope = coroutineScope?: CoroutineScope(Dispatchers.Main)

    private val _weekOrMonthFlow = MutableStateFlow<RestClientResult<List<WeekOrMonthData>>>(
        RestClientResult.none())
    val weekOrMonthFlow: CStateFlow<RestClientResult<List<WeekOrMonthData>>>
        get() = _weekOrMonthFlow.toCommonStateFlow()

    private val _weekOrMonthLocalObjectFlow = MutableStateFlow<WeekOrMonthData?>(null)
    val weekOrMonthLocalObjectFlow: CStateFlow<WeekOrMonthData?>
        get() = _weekOrMonthLocalObjectFlow.toCommonStateFlow()

    private val _updateGoldSipDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>()
    val updateGoldSipDetailsFlow:
            CFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>
         get() = _updateGoldSipDetailsFlow.toCommonFlow()

    fun fetchWeekOrMonth(
        sipSubscriptionType: com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType,
        recommendedDay: Int,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            _weekOrMonthFlow.emit(RestClientResult.loading())
            when (sipSubscriptionType) {
                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> {
                    val list = weekGenerator.getWeekList(recommendedDay)
                    _weekOrMonthFlow.emit(RestClientResult.success(list))
                    _weekOrMonthLocalObjectFlow.emit(list.find { it.isSelected })
                }
                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> {
                    val list = monthGenerator.getMonthList(recommendedDay)
                    _weekOrMonthFlow.emit(RestClientResult.success(list))
                    _weekOrMonthLocalObjectFlow.emit(list.find { it.isSelected })
                }
            }
        }
    }

    fun updateListOnItemClick(list: List<WeekOrMonthData>, position: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            val newList = ArrayList(list.map { it.copy() })
            if (newList[position].isSelected) {
                newList[position].isSelected = false
                _weekOrMonthLocalObjectFlow.emit(null)
            } else {
                _weekOrMonthLocalObjectFlow.emit(newList[position])
                newList.filter { it.isSelected }.map { it.isSelected = false }
                newList[position].isSelected = true
            }
            _weekOrMonthFlow.emit(RestClientResult.success(newList))
        }
    }

    fun updateGoldSip(updateSipDetails: com.jar.app.feature_gold_sip.shared.domain.model.UpdateSipDetails) {
        viewModelScope.launch {
            updateGoldSipDetailsUseCase.updateGoldSipDetails(updateSipDetails).collect {
                _updateGoldSipDetailsFlow.emit(it)
            }
        }
    }
}