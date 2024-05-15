package com.jar.app.feature_gold_sip.shared.ui

import com.jar.app.core_base.util.orZero
import com.jar.app.feature_gold_sip.shared.domain.model.WeekOrMonthData
import com.jar.app.feature_gold_sip.shared.util.MonthGenerator
import com.jar.app.feature_gold_sip.shared.util.WeekGenerator
import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipTypeSetupInfoUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase
import com.jar.app.feature_user_api.domain.mappers.toUserGoldSipDetails
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UpdateSipViewModel constructor(
    private val monthGenerator: MonthGenerator,
    private val weekGenerator: WeekGenerator,
    private val updateGoldSipDetailsUseCase: UpdateGoldSipDetailsUseCase,
    private val fetchGoldSipDetailsUseCase: FetchGoldSipDetailsUseCase,
    private val fetchGoldSipTypeSetupInfoUseCase: FetchGoldSipTypeSetupInfoUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)
    private val _weekOrMonthFlow = MutableSharedFlow<RestClientResult<List<WeekOrMonthData>>>()
    val weekOrMonthFlow: CFlow<RestClientResult<List<WeekOrMonthData>>>
        get() = _weekOrMonthFlow.toCommonFlow()

    private val _weekOrMonthLocalObjectFlow = MutableStateFlow<WeekOrMonthData?>(null)
    val weekOrMonthLocalObjectFlow: CStateFlow<WeekOrMonthData?>
        get() = _weekOrMonthLocalObjectFlow.toCommonStateFlow()

    private val _updateGoldSipDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>()
    val updateGoldSipDetailsFlow:
            CFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>
        get() = _updateGoldSipDetailsFlow.toCommonFlow()

    private val _goldSipDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails?>>>()
    val goldSipDetailsFlow: CFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails?>>>
        get() = _goldSipDetailsFlow.toCommonFlow()

    private val _fetchSetupGoldSipFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_gold_sip.shared.domain.model.GoldSipSetupInfo>>>()
    val fetchSetupGoldSipFlow:
            CFlow<RestClientResult<ApiResponseWrapper<com.jar.app.feature_gold_sip.shared.domain.model.GoldSipSetupInfo>>>
        get() = _fetchSetupGoldSipFlow.toCommonFlow()

    var recommendedValue = 0

    fun fetchSetupGoldSipData(subscriptionType: com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType) {
        viewModelScope.launch {
            fetchGoldSipTypeSetupInfoUseCase.fetchGoldSipTypeSetupInfo(subscriptionType.name)
                .collect {
                    _fetchSetupGoldSipFlow.emit(it)
                }
        }
    }

    fun fetchGoldSipDetails() {
        viewModelScope.launch {
            fetchGoldSipDetailsUseCase.fetchGoldSipDetails()
                .mapToDTO {
                    it?.toUserGoldSipDetails()
                }
                .collect {
                    _goldSipDetailsFlow.emit(it)
                }
        }
    }

    fun fetchWeekOrMonth(
        sipSubscriptionType: com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType,
        recommendedValue: Int,
        isSipSubscriptionTypeChanged: Boolean = false
    ) {
        this.recommendedValue = recommendedValue
        viewModelScope.launch(Dispatchers.Default) {
            _weekOrMonthFlow.emit(RestClientResult.loading())
            when (sipSubscriptionType) {
                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> {
                    val list = weekGenerator.getWeekList(recommendedValue)
                    _weekOrMonthFlow.emit(RestClientResult.success(list))
                    val item = list.find { it.isSelected }
                    if (isSipSubscriptionTypeChanged)
                        _weekOrMonthLocalObjectFlow.emit(item)
                    else
                        _weekOrMonthLocalObjectFlow.emit(if (item?.value.orZero() == recommendedValue) null else item)
                }

                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> {
                    val list = monthGenerator.getMonthList(recommendedValue)
                    _weekOrMonthFlow.emit(RestClientResult.success(list))
                    val item = list.find { it.isSelected }
                    if (isSipSubscriptionTypeChanged)
                        _weekOrMonthLocalObjectFlow.emit(item)
                    else
                        _weekOrMonthLocalObjectFlow.emit(if (item?.value.orZero() == recommendedValue) null else item)

                }
            }
        }
    }

    fun updateListOnItemClick(
        list: List<WeekOrMonthData>, position: Int, isSipSubscriptionTypeChanged: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val newList = ArrayList(list.map { it.copy() })
            if (newList[position].isSelected) {
                _weekOrMonthLocalObjectFlow.emit(null)
                newList[position].isSelected = false
            } else {
                newList.filter { it.isSelected }.map { it.isSelected = false }
                newList[position].isSelected = true
                _weekOrMonthLocalObjectFlow.emit(if (isSipSubscriptionTypeChanged && recommendedValue == newList[position].value) null else newList[position])
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
