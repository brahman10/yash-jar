package com.jar.app.feature_lending.impl.ui.realtime_flow.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchRealTimeLeadStatusUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.ui.realtime_flow.landing.RealTimeReadyCashLandingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class RealTimeReadyCashLandingViewModelAndroid @Inject constructor(
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val fetchRealTimeLeadStatusUseCase: FetchRealTimeLeadStatusUseCase
) : ViewModel() {


    private val viewModel by lazy {
        RealTimeReadyCashLandingViewModel(
            fetchStaticContentUseCase = fetchStaticContentUseCase,
            fetchRealTimeLeadStatusUseCase = fetchRealTimeLeadStatusUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}