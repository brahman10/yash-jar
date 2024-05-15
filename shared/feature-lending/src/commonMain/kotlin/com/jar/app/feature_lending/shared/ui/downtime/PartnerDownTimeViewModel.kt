package com.jar.app.feature_lending.shared.ui.downtime

import com.jar.app.feature_lending.shared.domain.model.v2.StaticContentResponse
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateNotifyUserUseCase
import com.jar.app.feature_lending.shared.util.LendingConstants
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

class PartnerDownTimeViewModel constructor(
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val updateNotifyUserUseCase: UpdateNotifyUserUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _staticContentFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>(
            RestClientResult.none()
        )
    val staticContentFlow: CStateFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>
        get() = _staticContentFlow.toCommonStateFlow()

    private val _notifyUserFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>(RestClientResult.none())
    val notifyUserFlow: CStateFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _notifyUserFlow.toCommonStateFlow()


    fun fetchStaticContentForDownTime(loanId: String) {
        viewModelScope.launch {
            fetchStaticContentUseCase.fetchLendingStaticContent(
                loanId,
                LendingConstants.StaticContentType.APP_UNDER_MAINTENANCE
            ).collect {
                _staticContentFlow.emit(it)
            }
        }
    }

    fun updateNotifyUsers() {
        viewModelScope.launch {
            updateNotifyUserUseCase.updateNotifyUser().collect {
                _notifyUserFlow.emit(it)
            }
        }
    }
}