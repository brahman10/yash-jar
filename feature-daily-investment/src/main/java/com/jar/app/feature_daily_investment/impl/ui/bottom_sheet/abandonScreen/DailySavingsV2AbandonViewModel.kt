package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.abandonScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_daily_investment.shared.domain.model.AbandonScreenBottomSheetResponse
import com.jar.app.feature_daily_investment.shared.domain.use_case.FetchDSAbandonScreenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DailySavingsV2AbandonViewModel @Inject constructor(
    private val fetchDSAbandonScreenUseCase: FetchDSAbandonScreenUseCase
): ViewModel(
) {
    private val _bottomSheetLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<AbandonScreenBottomSheetResponse>>>()
    val bottomSheetLiveData: LiveData<RestClientResult<ApiResponseWrapper<AbandonScreenBottomSheetResponse>>> get() = _bottomSheetLiveData
    fun fetchBottomSheetData(
        contentType : String
    ) {
        viewModelScope.launch {
            fetchDSAbandonScreenUseCase.fetchAbandonBottomSheetData(contentType).collect {
                _bottomSheetLiveData.postValue(it)
            }
        }
    }
}
