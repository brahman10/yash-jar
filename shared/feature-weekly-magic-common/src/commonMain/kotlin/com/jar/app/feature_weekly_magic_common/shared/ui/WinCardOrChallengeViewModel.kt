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

class WinCardOrChallengeViewModel constructor(
    private val fetchWeeklyChallengeDetailUseCase: FetchWeeklyChallengeDetailUseCase,
    private val markWeeklyChallengeViewedUseCase: MarkWeeklyChallengeViewedUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _weeklyChallengeFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeDetail?>>>(RestClientResult.none())
    val weeklyChallengeFlow: CStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeDetail?>>>
        get() = _weeklyChallengeFlow.toCommonStateFlow()

    private val _markWeeklyChallengeWinFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val markWeeklyChallengeWinFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _markWeeklyChallengeWinFlow.toCommonStateFlow()

    fun fetchWeeklyChallengeDetailById(challengeId: String) {
        viewModelScope.launch {
            fetchWeeklyChallengeDetailUseCase.fetchWeeklyChallengeDetailById(challengeId).collect {
                _weeklyChallengeFlow.emit(it)
            }
        }
    }

    fun markWeeklyChallengeWinViewed(challengeId: String) {
        viewModelScope.launch {
            markWeeklyChallengeViewedUseCase.markCurrentWeeklyChallengeViewed(challengeId).collect {
                _markWeeklyChallengeWinFlow.emit(it)
            }
        }
    }
}