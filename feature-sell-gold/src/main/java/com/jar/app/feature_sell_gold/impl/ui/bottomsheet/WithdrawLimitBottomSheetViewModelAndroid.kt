package com.jar.app.feature_sell_gold.impl.ui.bottomsheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchWithdrawalBottomSheetDataUseCase
import com.jar.app.feature_sell_gold.shared.ui.bottomsheet.WithdrawLimitBottomSheetViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class WithdrawLimitBottomSheetViewModelAndroid @Inject constructor(
    fetchWithdrawalBottomSheetDataUseCase: IFetchWithdrawalBottomSheetDataUseCase,
    analyticsApi: AnalyticsApi
) : ViewModel() {
    private val viewModel by lazy {
        WithdrawLimitBottomSheetViewModel(
            fetchWithdrawalBottomSheetDataUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}