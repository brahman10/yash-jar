package com.jar.app.feature_gold_sip.impl.ui.sip_day_or_date

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase
import com.jar.app.feature_gold_sip.shared.ui.SelectSipDayOrDateViewModel
import com.jar.app.feature_gold_sip.shared.util.MonthGenerator
import com.jar.app.feature_gold_sip.shared.util.WeekGenerator
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SelectSipDayOrDateViewModelAndroid @Inject constructor(
    private val monthGenerator: MonthGenerator,
    private val weekGenerator: WeekGenerator,
    private val updateGoldSipDetailsUseCase: UpdateGoldSipDetailsUseCase,
    private val analyticsApi: AnalyticsApi,
) : ViewModel() {


    private val viewModel by lazy {
        SelectSipDayOrDateViewModel(
            monthGenerator,
            weekGenerator,
            updateGoldSipDetailsUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}