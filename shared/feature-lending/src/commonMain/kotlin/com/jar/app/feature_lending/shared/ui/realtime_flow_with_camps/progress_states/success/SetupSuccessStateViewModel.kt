package com.jar.app.feature_lending.shared.ui.realtime_flow_with_camps.progress_states.success

import com.jar.app.feature_lending.shared.domain.use_case.FetchPANStatusUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SetupSuccessStateViewModel constructor(
    private val fetchPANStatusUseCase: FetchPANStatusUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _panStatusData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val panStatusData: SharedFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _panStatusData.asSharedFlow()


    fun fetchPanStatus() {
        viewModelScope.launch {
            fetchPANStatusUseCase.fetchPANStatus().collectLatest {
                _panStatusData.emit(it)
            }
        }
    }
}