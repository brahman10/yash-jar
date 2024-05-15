package com.jar.app.feature_lending.shared.ui.onboarding

import com.jar.app.feature_lending.shared.domain.model.v2.LandingScreenContentResponse
import com.jar.app.feature_lending.shared.domain.use_case.FetchReadyCashLandingScreenContentUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class ReadyCashLandingViewModel constructor(
    private val fetchReadyCashLandingScreenContent: FetchReadyCashLandingScreenContentUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _screenContentFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<LandingScreenContentResponse?>>>(RestClientResult.none())
    val screenContentFlow: CStateFlow<RestClientResult<ApiResponseWrapper<LandingScreenContentResponse?>>>
        get() = _screenContentFlow.toCommonStateFlow()

    fun fetchScreenContentContent() {
        viewModelScope.launch {
            fetchReadyCashLandingScreenContent.fetchReadyCashLandingScreenContent().collect {
                _screenContentFlow.emit(it)
            }
        }
    }

}