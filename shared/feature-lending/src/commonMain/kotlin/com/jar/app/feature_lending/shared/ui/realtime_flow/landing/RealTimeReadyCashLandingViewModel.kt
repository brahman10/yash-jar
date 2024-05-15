package com.jar.app.feature_lending.shared.ui.realtime_flow.landing

import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.RealTimeLeadStatus
import com.jar.app.feature_lending.shared.domain.model.v2.StaticContentResponse
import com.jar.app.feature_lending.shared.domain.use_case.FetchRealTimeLeadStatusUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RealTimeReadyCashLandingViewModel constructor(
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val fetchRealTimeLeadStatusUseCase: FetchRealTimeLeadStatusUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    var leadStatus: RealTimeLeadStatus? = null

    private val _realTimeStaticContent =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>(
            RestClientResult.none()
        )
    val realTimeStaticContent = _realTimeStaticContent.asStateFlow()


    fun getLandingData() {
        viewModelScope.launch {
            fetchStaticContentUseCase.fetchLendingStaticContent(
                null,
                LendingConstants.StaticContentType.REALTIME_LENDING
            ).collect {
                _realTimeStaticContent.value = it
            }
        }
    }

    fun fetchLeadStatus() {
        viewModelScope.launch {
            fetchRealTimeLeadStatusUseCase.fetchRealTimeLeadStatus().collect(
                onLoading = {

                },
                onSuccess = {
                    leadStatus = it
                },
                onError = { message, errorCode ->

                }
            )
        }
    }
}