package com.jar.app.feature_weekly_magic_common.shared.ui

import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeDetail
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeDetailUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeViewedUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WeeklyChallengeStoryViewModel constructor(
    private val fetchWeeklyChallengeDetailUseCase: FetchWeeklyChallengeDetailUseCase,
    private val markWeeklyChallengeViewedUseCase: MarkWeeklyChallengeViewedUseCase,
    coroutineScope: CoroutineScope?
){

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _weeklyChallengeDetailFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeDetail?>>>(RestClientResult.none())
    val weeklyChallengeDetailFlow: CStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeDetail?>>>
        get() = _weeklyChallengeDetailFlow.toCommonStateFlow()

    private val _markWeeklyChallengeViewedFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val markWeeklyChallengeViewedFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _markWeeklyChallengeViewedFlow.toCommonStateFlow()

    fun fetchWeeklyChallengeDetails(challengeId: String? = null) {
        viewModelScope.launch {
            challengeId?.takeIf { it.isNotBlank() }?.let {
                fetchWeeklyChallengeDetailUseCase.fetchWeeklyChallengeDetailById(it)
                    .collect { result ->
                        _weeklyChallengeDetailFlow.emit(result)
                    }
            } ?: kotlin.run {
                fetchWeeklyChallengeDetailUseCase.fetchWeeklyChallengeDetailForToday()
                    .collect { result ->
                        _weeklyChallengeDetailFlow.emit(result)
                    }
            }
        }
    }

    fun markPreviousWeeklyChallengeStoryViewed(challengeId: String) {
        viewModelScope.launch {
            markWeeklyChallengeViewedUseCase.markPreviousWeeklyChallengeStoryViewed(challengeId)
                .collect {
                    _markWeeklyChallengeViewedFlow.emit(it)
                }
        }
    }

}