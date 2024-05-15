package com.jar.app.feature_sell_gold.shared.ui.bottomsheet

import com.jar.app.feature_sell_gold.shared.domain.models.WithdrawHelpData
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchWithdrawalBottomSheetDataUseCase
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.ButtonClicked
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.ViewWithdrawalHistory
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.Withdrawal_LimitExhaustedBS_Clicked
import com.jar.app.feature_sell_gold.shared.utils.SellGoldEvent.Withdrawal_LimitExhaustedBS_Shown
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WithdrawLimitBottomSheetViewModel(
    private val fetchWithdrawalBottomSheetDataUseCase: IFetchWithdrawalBottomSheetDataUseCase,
    private val analytics: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main.immediate)

    private val _bottomSheetLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<WithdrawHelpData?>>>(RestClientResult.none())
    val bottomSheetLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<WithdrawHelpData?>>>
        get() = _bottomSheetLiveData.toCommonStateFlow()

    private val _withdrawBottomSheetFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<WithdrawHelpData?>>>(
            RestClientResult.none()
        )
    val withdrawBottomSheetFlow: CStateFlow<RestClientResult<ApiResponseWrapper<WithdrawHelpData?>>>
        get() = _withdrawBottomSheetFlow.toCommonStateFlow()

    init {
        fetchBottomSheetData()
    }

    fun fetchBottomSheetData() {
        viewModelScope.launch {
            fetchWithdrawalBottomSheetDataUseCase.fetchWithdrawBottomSheetData()
                .collect { response ->
                    _withdrawBottomSheetFlow.update { response }
                }
        }
    }

    fun postWithdrawLimitBottomSheetShownEvent() {
        analytics.postEvent(Withdrawal_LimitExhaustedBS_Shown)
    }

    fun postViewWithdrawalHistoryButtonClickedEvent() {
        analytics.postEvent(Withdrawal_LimitExhaustedBS_Clicked, ButtonClicked, ViewWithdrawalHistory)
    }
}