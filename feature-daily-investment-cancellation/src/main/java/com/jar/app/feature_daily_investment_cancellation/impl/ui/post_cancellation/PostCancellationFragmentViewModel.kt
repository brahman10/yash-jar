package com.jar.app.feature_daily_investment_cancellation.impl.ui.post_cancellation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPostCancellationData
import com.jar.app.feature_daily_investment_cancellation.shared.domain.use_case.FetchDailyInvestmentPostCancellationDataUseCase
import com.jar.app.feature_user_api.data.dto.UserGoldSipDetailsDTO
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PostCancellationFragmentViewModel @Inject constructor(
    private val fetchDailyInvestmentPostCancellationDataUseCase: FetchDailyInvestmentPostCancellationDataUseCase,
    private val fetchGoldSipDetailsUseCase: FetchGoldSipDetailsUseCase
) : ViewModel(
) {
    private val _cancellationFragmentFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentPostCancellationData>>>(
            RestClientResult.none()
        )
    val cancellationFragmentFlow: CStateFlow<RestClientResult<ApiResponseWrapper<DailyInvestmentPostCancellationData>>>
        get() = _cancellationFragmentFlow.toCommonStateFlow()

    private val _userGoldDetailsLDFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetailsDTO>>>(
            RestClientResult.none()
        )
    val userGoldDetailsLDFlow: CStateFlow<RestClientResult<ApiResponseWrapper<UserGoldSipDetailsDTO>>>
        get() = _userGoldDetailsLDFlow.toCommonStateFlow()

    fun fetchCancellationFragmentDataFlow() {
        viewModelScope.launch {
            fetchDailyInvestmentPostCancellationDataUseCase.fetchDailyInvestmentPostCancellationData()
                .collectLatest {
                    _cancellationFragmentFlow.emit(it)
                }
        }
    }

    fun navigateFromSaveWeeklyFlow() {
        viewModelScope.launch {
            fetchGoldSipDetailsUseCase.fetchGoldSipDetails()
                .collectLatest {
                    _userGoldDetailsLDFlow.emit(it)
                }
        }
    }
}
