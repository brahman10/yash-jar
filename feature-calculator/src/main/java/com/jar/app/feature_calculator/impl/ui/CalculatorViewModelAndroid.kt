package com.jar.app.feature_calculator.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_calculator.shared.domain.use_case.FetchCalculatorDataUseCase
import com.jar.app.feature_calculator.shared.ui.CalculatorViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class CalculatorViewModelAndroid @Inject constructor(
    private val fetchCalculatorDataUseCase: FetchCalculatorDataUseCase,
    private val analyticsApi: AnalyticsApi,
) : ViewModel() {

    private val viewModel by lazy {
        CalculatorViewModel(
            fetchCalculatorDataUseCase = fetchCalculatorDataUseCase,
            analyticsApi = analyticsApi,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}