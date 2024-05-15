package com.jar.app.feature_gold_sip.shared.ui

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_user_api.domain.mappers.toUserGoldSipDetails
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdatePauseSavingUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GoldSipDetailsViewModel constructor(
    private val updateGoldSipDetailsUseCase: UpdateGoldSipDetailsUseCase,
    private val fetchGoldSipDetailsUseCase: FetchGoldSipDetailsUseCase,
    private val updatePauseSavingUseCase: UpdatePauseSavingUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _updateGoldSipDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>()
    val updateGoldSipDetailsFlow: CFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>
        get() = _updateGoldSipDetailsFlow.toCommonFlow()

    private val _goldSipDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails?>>>(RestClientResult.none())
    val goldSipDetailsFlow: CFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails?>>>
        get() = _goldSipDetailsFlow.toCommonFlow()

    private val _sipPausedFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>()
    val sipPausedFlow: CFlow<RestClientResult<ApiResponseWrapper<PauseSavingResponse>>>
        get() = _sipPausedFlow.toCommonFlow()

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

    fun updateGoldSip(updateSipDetails: com.jar.app.feature_gold_sip.shared.domain.model.UpdateSipDetails) {
        viewModelScope.launch {
            updateGoldSipDetailsUseCase.updateGoldSipDetails(updateSipDetails).collect {
                _updateGoldSipDetailsFlow.emit(it)
            }
        }
    }

    fun resumeSip() {
        viewModelScope.launch {
            updatePauseSavingUseCase.updatePauseSavingValue(
                shouldPause = false,
                pauseType = SavingsType.GOLD_SIPS.name
            ).collect { _sipPausedFlow.emit(it) }
        }
    }

    fun fireGoldSipDetailEvent(
        eventName: String,
        eventParamsMap: MutableMap<String, Any>?
    ) {
        eventParamsMap?.let {
            analyticsApi.postEvent(eventName, it)
        } ?: kotlin.run {
            analyticsApi.postEvent(eventName)
        }
    }
}