package com.jar.app.feature_homepage.shared.ui.help_videos

import com.jar.app.feature_homepage.shared.domain.model.HelpVideosResponse
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHelpVideosUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HelpVideosViewModel constructor(
    private val fetchHelpVideosUseCase: FetchHelpVideosUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _helpVideosLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<HelpVideosResponse>>>(RestClientResult.none())
    val helpVideosLiveData: CFlow<RestClientResult<ApiResponseWrapper<HelpVideosResponse>>>
        get() = _helpVideosLiveData.toCommonFlow()

    fun fetchHelpVideos(language: String = "en") {
        viewModelScope.launch {
            fetchHelpVideosUseCase.fetchHelpVideos(language).collect {
                _helpVideosLiveData.emit(it)
            }
        }
    }
}