package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.ds_intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentIntroData
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDailySavingsIntroBottomSheetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DailySavingsV2IntroductionViewModel @Inject constructor(
    private val fetchDailySavingsIntroBottomSheetUseCase: FetchDailySavingsIntroBottomSheetUseCase
): ViewModel(
) {
    private val _bottomSheetLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentIntroData>>>()
    val bottomSheetLiveData: LiveData<RestClientResult<ApiResponseWrapper<DailyInvestmentIntroData>>> get() = _bottomSheetLiveData

    fun fetchBottomSheetData() {
        viewModelScope.launch {
            fetchDailySavingsIntroBottomSheetUseCase.fetchDailySavingsInto().collect {
                _bottomSheetLiveData.postValue(it)
            }
        }
    }
}
