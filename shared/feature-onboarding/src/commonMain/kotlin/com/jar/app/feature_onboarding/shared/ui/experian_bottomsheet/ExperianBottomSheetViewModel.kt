package com.jar.app.feature_onboarding.shared.ui.experian_bottomsheet

import com.jar.app.feature_onboarding.shared.domain.model.ExperianTCResponse
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchExperianTCUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class ExperianBottomSheetViewModel constructor(
    private val fetchExperianTCUseCase: FetchExperianTCUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _bottomSheetDataFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ExperianTCResponse>>>(RestClientResult.none())
    val bottomSheetDataFlow: CFlow<RestClientResult<ApiResponseWrapper<ExperianTCResponse>>>
        get() = _bottomSheetDataFlow.shareIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        ).toCommonFlow()

    fun fetchBottomSheetData() {
        viewModelScope.launch {
            fetchExperianTCUseCase.fetchExperianTC().collect {
                _bottomSheetDataFlow.emit(it)
            }
        }
    }
}