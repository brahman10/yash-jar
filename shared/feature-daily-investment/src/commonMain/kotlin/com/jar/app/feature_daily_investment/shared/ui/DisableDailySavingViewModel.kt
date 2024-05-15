package com.jar.app.feature_daily_investment.shared.ui

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_savings_common.shared.domain.use_case.DisableUserSavingsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class DisableDailySavingViewModel constructor(
    private val disableUserSavingsUseCase: DisableUserSavingsUseCase,
    private val analyticsApi: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _disableDailySavingFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>()
    val disableDailySavingFlow: CFlow<RestClientResult<ApiResponseWrapper<UserSavingsDetails>>>
        get() = _disableDailySavingFlow.toCommonFlow()

    fun disableDailySavings() {
        viewModelScope.launch {
            disableUserSavingsUseCase.disableSavings(SavingsType.DAILY_SAVINGS).collect {
                _disableDailySavingFlow.emit(it)
            }
        }
    }
}