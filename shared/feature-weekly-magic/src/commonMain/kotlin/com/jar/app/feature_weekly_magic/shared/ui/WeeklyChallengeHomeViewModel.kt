package com.jar.app.feature_weekly_magic.shared.ui

import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeDetail
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeDetailUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeViewedUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WeeklyChallengeHomeViewModel constructor(
    private val fetchWeeklyChallengeDetailUseCase: FetchWeeklyChallengeDetailUseCase,
    private val fetchWeeklyChallengeMetaDataUseCase: FetchWeeklyChallengeMetaDataUseCase,
    private val markWeeklyChallengeViewedUseCase: MarkWeeklyChallengeViewedUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _weeklyChallengeDetailFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeDetail?>>>(RestClientResult.none())
    val weeklyChallengeDetailFlow: CStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeDetail?>>>
        get() = _weeklyChallengeDetailFlow.toCommonStateFlow()

    private val _weeklyChallengeDetailByIdFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeDetail?>>>(RestClientResult.none())
    val weeklyChallengeDetailByIdFlow: CStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeDetail?>>>
        get() = _weeklyChallengeDetailByIdFlow.toCommonStateFlow()

    private val _weeklyChallengeMetaDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeMetaData?>>>(RestClientResult.none())
    val weeklyChallengeMetaDataFlow: CStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeMetaData?>>>
        get() = _weeklyChallengeMetaDataFlow.toCommonStateFlow()

    private val _markCurrentChallengeWonFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val markCurrentChallengeWonFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _markCurrentChallengeWonFlow.toCommonStateFlow()

    val markPreviousChallengeViewedFlow = MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())

    fun fetchWeeklyChallengeDetails() {
        viewModelScope.launch {
            fetchWeeklyChallengeDetailUseCase.fetchWeeklyChallengeDetailForToday()
                .collect { result ->
                    _weeklyChallengeDetailFlow.emit(result)
                }
        }
    }

    fun fetchWeeklyChallengeDetails(challengeId:String) {
        viewModelScope.launch {
            fetchWeeklyChallengeDetailUseCase.fetchWeeklyChallengeDetailById(challengeId)
                .collect { result ->
                    _weeklyChallengeDetailByIdFlow.emit(result)
                }
        }
    }


    fun fetchWeeklyChallengeMetaData() {
        viewModelScope.launch {
            fetchWeeklyChallengeMetaDataUseCase.fetchWeeklyChallengeMetaData(false).collect {
                _weeklyChallengeMetaDataFlow.emit(it)
            }
        }
    }

    fun markChallengeWon(currentChallengeId: String) {
        viewModelScope.launch {
            markWeeklyChallengeViewedUseCase.markCurrentWeeklyChallengeViewed(currentChallengeId).collect {
                _markCurrentChallengeWonFlow.emit(it)
            }
        }
    }
    fun markPreviousChallengeViewed(currentChallengeId: String) {
        viewModelScope.launch {
            markWeeklyChallengeViewedUseCase.markPreviousWeeklyChallengeStoryViewed(currentChallengeId).collect {
                markPreviousChallengeViewedFlow.emit(it)
            }
        }
    }
}