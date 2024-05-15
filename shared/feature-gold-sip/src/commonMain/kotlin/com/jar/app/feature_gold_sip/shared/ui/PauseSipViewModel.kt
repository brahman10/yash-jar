package com.jar.app.feature_gold_sip.shared.ui

import com.jar.app.feature_user_api.domain.model.PauseSavingOption
import com.jar.app.feature_user_api.domain.model.PauseSavingOptionWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_user_api.domain.model.PauseSavingResponse
import com.jar.app.feature_user_api.domain.use_case.UpdatePauseSavingUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper as LibraryApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult as LibraryRestClientResult

class PauseSipViewModel constructor(
    private val updatePauseSavingUseCase: UpdatePauseSavingUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _pauseOptionsFlow =
        MutableStateFlow<RestClientResult<List<PauseSavingOptionWrapper>>>(RestClientResult.none())
    val pauseOptionsFlow: CStateFlow<RestClientResult<List<PauseSavingOptionWrapper>>>
        get() = _pauseOptionsFlow.toCommonStateFlow()

    private val _sipPausedFlow =
        MutableSharedFlow<LibraryRestClientResult<LibraryApiResponseWrapper<PauseSavingResponse>>>()
    val sipPausedFlow: CFlow<LibraryRestClientResult<LibraryApiResponseWrapper<PauseSavingResponse>>>
        get() = _sipPausedFlow.toCommonFlow()

    var pauseSavingOptionWrapper: PauseSavingOptionWrapper? = null

    fun fetchPauseOptions(sipSubscriptionType: com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType) {
        viewModelScope.launch {
            _pauseOptionsFlow.emit(RestClientResult.loading())
            when (sipSubscriptionType) {
                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> {
                    pauseSavingOptionWrapper =
                        PauseSavingOptionWrapper(PauseSavingOption.WEEK, isSelected = true)
                    _pauseOptionsFlow.emit(
                        RestClientResult.success(
                            listOf(
                                PauseSavingOptionWrapper(PauseSavingOption.WEEK, isSelected = true),
                                PauseSavingOptionWrapper(PauseSavingOption.TWO_WEEKS),
                                PauseSavingOptionWrapper(PauseSavingOption.THREE_WEEKS),
                                PauseSavingOptionWrapper(PauseSavingOption.FOUR_WEEKS),
                            )
                        )
                    )
                }

                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> {
                    pauseSavingOptionWrapper = PauseSavingOptionWrapper(PauseSavingOption.MONTH)
                    _pauseOptionsFlow.emit(
                        RestClientResult.success(
                            listOf(
                                PauseSavingOptionWrapper(
                                    PauseSavingOption.MONTH
                                )
                            )
                        )
                    )
                }
            }
        }
    }

    fun updatePauseOptionListOnItemClick(list: List<PauseSavingOptionWrapper>, position: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            val newList = ArrayList(list.map { it.copy() })
            if (newList[position].isSelected) {
                newList[position].isSelected = false
                pauseSavingOptionWrapper = null
            } else {
                newList.filter { it.isSelected }.map { it.isSelected = false }
                newList[position].isSelected = true
                pauseSavingOptionWrapper = newList[position]
            }
            _pauseOptionsFlow.emit(RestClientResult.success(newList))
        }
    }

    fun pauseSip(shouldPause: Boolean, pauseType: String, pauseDuration: String) {
        viewModelScope.launch {
            updatePauseSavingUseCase.updatePauseSavingValue(
                shouldPause = shouldPause,
                pauseType = pauseType,
                pauseDuration = pauseDuration
            ).collect { _sipPausedFlow.emit(it) }
        }
    }

    fun firePauseSipEvent(
        eventName: String,
        eventParamsMap: Map<String, Any>? = null
    ) {
        eventParamsMap?.let {
            analyticsApi.postEvent(eventName, it)
        } ?: kotlin.run {
            analyticsApi.postEvent(eventName)
        }
    }

}