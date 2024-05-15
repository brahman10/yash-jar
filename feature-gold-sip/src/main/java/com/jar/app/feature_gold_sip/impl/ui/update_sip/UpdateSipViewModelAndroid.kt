package com.jar.app.feature_gold_sip.impl.ui.update_sip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_sip.shared.util.MonthGenerator
import com.jar.app.feature_gold_sip.shared.util.WeekGenerator
import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipTypeSetupInfoUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase
import com.jar.app.feature_gold_sip.shared.ui.UpdateSipViewModel
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class UpdateSipViewModelAndroid @Inject constructor(
    private val monthGenerator: MonthGenerator,
    private val weekGenerator: WeekGenerator,
    private val updateGoldSipDetailsUseCase: UpdateGoldSipDetailsUseCase,
    private val fetchGoldSipDetailsUseCase: FetchGoldSipDetailsUseCase,
    private val fetchGoldSipTypeSetupInfoUseCase: FetchGoldSipTypeSetupInfoUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        UpdateSipViewModel(
            monthGenerator,
            weekGenerator,
            updateGoldSipDetailsUseCase,
            fetchGoldSipDetailsUseCase,
            fetchGoldSipTypeSetupInfoUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}