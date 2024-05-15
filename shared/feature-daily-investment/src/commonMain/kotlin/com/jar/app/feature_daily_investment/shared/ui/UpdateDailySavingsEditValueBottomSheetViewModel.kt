package com.jar.app.feature_daily_investment.shared.ui

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_daily_investment.shared.domain.model.SuggestedRecurringAmount
import com.jar.app.feature_savings_common.shared.domain.model.DSSavingsState.*
import com.jar.app.feature_savings_common.shared.domain.model.SavingSetupInfo
import com.jar.app.feature_savings_common.shared.domain.model.SavingsSubscriptionType.*
import com.jar.app.feature_savings_common.shared.domain.model.SavingsType.*
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchSavingsSetupInfoUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class UpdateDailySavingsEditValueBottomSheetViewModel constructor(
    private val fetchSavingsSetupInfoUseCase: FetchSavingsSetupInfoUseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _dsAmountFlowData: MutableStateFlow<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>> = MutableStateFlow(
    RestClientResult.none())
    val dsAmountFlowData: CStateFlow<RestClientResult<ApiResponseWrapper<SavingSetupInfo>>> =
        _dsAmountFlowData.toCommonStateFlow()

    private val _rVFlowData = MutableStateFlow<List<SuggestedRecurringAmount>>(emptyList())
    val rVFlowData: CStateFlow<List<SuggestedRecurringAmount>>
        get() = _rVFlowData.toCommonStateFlow()

    fun fetchDSAmountData() {
        viewModelScope.launch {
            fetchSavingsSetupInfoUseCase.fetchSavingSetupInfo(
                DEFAULT,
                DAILY_SAVINGS,
                DS_UPDATE.name
            ).collect {
                _dsAmountFlowData.emit(it)
            }
        }
    }

    fun createRvListData(savingSetupInfo: SavingSetupInfo) {
        viewModelScope.launch{
            val list = mutableListOf<SuggestedRecurringAmount>()
            savingSetupInfo.options.forEach {
                list.add(SuggestedRecurringAmount(it.amount,it.recommended))
            }
            _rVFlowData.emit(list)
        }
    }
}