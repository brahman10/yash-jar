package com.jar.app.feature_weekly_magic_common.shared.ui

import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeOnBoardedUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OnBoardingWeeklyChallengeViewModel constructor(
    private val markWeeklyChallengeOnBoardedUseCase: MarkWeeklyChallengeOnBoardedUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _weeklyChallengeOnBoardingCompletedFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val weeklyChallengeOnBoardingCompletedFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _weeklyChallengeOnBoardingCompletedFlow.toCommonStateFlow()

    fun markWeeklyChallengeOnBoardingCompleted() {
        viewModelScope.launch {
            markWeeklyChallengeOnBoardedUseCase.markWeeklyChallengeOnBoarded().collect {
                _weeklyChallengeOnBoardingCompletedFlow.emit(it)
            }
        }
    }
}