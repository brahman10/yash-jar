package com.jar.app.feature_weekly_magic.shared.ui

import com.jar.app.feature_weekly_magic.shared.domain.usecase.FetchWeeklyChallengeInfoUseCase
import com.jar.app.feature_weekly_magic.shared.domain.usecase.MarkWeeklyChallengeInfoAsViewedUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeInfo
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WeeklyChallengeInfoViewModel (
    private val fetchWeeklyChallengeInfoUseCase: FetchWeeklyChallengeInfoUseCase,
    private val markWeeklyChallengeInfoAsViewedUseCase: MarkWeeklyChallengeInfoAsViewedUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _weeklyChallengeInfoFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeInfo?>>>(RestClientResult.none())
    val weeklyChallengeInfoFlow: CStateFlow<RestClientResult<ApiResponseWrapper<WeeklyChallengeInfo?>>>
        get() = _weeklyChallengeInfoFlow.toCommonStateFlow()

    private val _markWeeklyChallengeAsViewedFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val markWeeklyChallengeAsViewedFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _markWeeklyChallengeAsViewedFlow.toCommonStateFlow()

    fun fetchWeeklyChallengeInfo() {
        viewModelScope.launch {
            fetchWeeklyChallengeInfoUseCase.fetchWeeklyChallengeInfo()
                .collect { result ->
                    _weeklyChallengeInfoFlow.emit(result)
                }
        }
    }

    fun markWeeklyChallengeAsViewed(challengeId: String) {
        viewModelScope.launch {
            markWeeklyChallengeInfoAsViewedUseCase.markWeeklyChallengeInfoAsViewed(challengeId)
                .collect { result ->
                    _markWeeklyChallengeAsViewedFlow.emit(result)
                }
        }
    }

}