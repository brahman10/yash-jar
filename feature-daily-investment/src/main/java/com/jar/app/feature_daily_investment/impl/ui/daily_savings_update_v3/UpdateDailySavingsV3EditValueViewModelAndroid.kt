package com.jar.app.feature_daily_investment.impl.ui.daily_savings_update_v3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_daily_investment.shared.ui.UpdateDailySavingsEditValueBottomSheetViewModel
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchSavingsSetupInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpdateDailySavingsV3EditValueViewModelAndroid @Inject constructor(
    private val fetchSavingsSetupInfoUseCase: FetchSavingsSetupInfoUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        UpdateDailySavingsEditValueBottomSheetViewModel(
            fetchSavingsSetupInfoUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}