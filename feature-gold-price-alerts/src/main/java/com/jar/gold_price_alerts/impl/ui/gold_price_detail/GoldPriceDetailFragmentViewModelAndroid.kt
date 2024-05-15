package com.jar.gold_price_alerts.impl.ui.gold_price_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldPriceTrendScreenStaticUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldTrendUseCase
import com.jar.feature_gold_price_alerts.shared.ui.GoldPriceDetailFragmentViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldPriceDetailFragmentViewModelAndroid @Inject constructor(
    private val fetchGoldTrendUseCase: FetchGoldTrendUseCase,
    private val fetchGoldPriceTrendScreenStaticUseCase: FetchGoldPriceTrendScreenStaticUseCase,
    private val analyticsApi: AnalyticsApi,
) : ViewModel() {

    private val viewModel by lazy {
        GoldPriceDetailFragmentViewModel(
            fetchGoldTrendUseCase,
            fetchGoldPriceTrendScreenStaticUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}