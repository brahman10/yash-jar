package com.jar.app.feature_gold_sip.shared.ui

import com.jar.app.feature_gold_sip.shared.domain.model.GoldSipSetupInfo
import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipTypeSetupInfoUseCase
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

class GoldSipTypeSelectionViewModel constructor(
    private val updateGoldSipDetailsUseCase: UpdateGoldSipDetailsUseCase,
    private val fetchGoldSipTypeSetupInfoUseCase: FetchGoldSipTypeSetupInfoUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _updateGoldSipDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>()
    val updateGoldSipDetailsFlow:
            CFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetails>>>
        get() = _updateGoldSipDetailsFlow.toCommonFlow()

    private val _fetchSetupGoldSipFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldSipSetupInfo>>>(RestClientResult.none())
    val fetchSetupGoldSipFlow:
            CStateFlow<RestClientResult<ApiResponseWrapper<GoldSipSetupInfo>>>
        get() = _fetchSetupGoldSipFlow.toCommonStateFlow()

    var currentlySelectedValue: Float? = null

    fun fetchSetupGoldSipData(subscriptionType: com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType) {
        currentlySelectedValue = null
        viewModelScope.launch {
            fetchGoldSipTypeSetupInfoUseCase.fetchGoldSipTypeSetupInfo(subscriptionType.name)
                .collect {
                    _fetchSetupGoldSipFlow.emit(it)
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

    fun fireSipSetupEvent(
        eventName: String,
        eventParamsMap: Map<String, Any>?
    ) {
        eventParamsMap?.let {
            analyticsApi.postEvent(eventName, it)
        } ?: kotlin.run {
            analyticsApi.postEvent(eventName)
        }
    }
}