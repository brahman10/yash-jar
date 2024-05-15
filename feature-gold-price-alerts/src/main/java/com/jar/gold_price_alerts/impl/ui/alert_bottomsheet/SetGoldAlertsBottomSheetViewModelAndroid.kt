package com.jar.gold_price_alerts.impl.ui.alert_bottomsheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.feature_gold_price_alerts.shared.domain.use_case.CreateGoldPriceAlertUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldPriceTrendBottomSheetStaticDataUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.GetLatestGoldPriceAlertUseCase
import com.jar.feature_gold_price_alerts.shared.ui.SetGoldAlertsBottomSheetViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SetGoldAlertsBottomSheetViewModelAndroid @Inject constructor(
    private val createGoldPriceAlertUseCase: CreateGoldPriceAlertUseCase,
    private val getLatestGoldPriceAlertUseCase: GetLatestGoldPriceAlertUseCase,
    private val fetchBottomSheetStaticDataUseCase: FetchGoldPriceTrendBottomSheetStaticDataUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        SetGoldAlertsBottomSheetViewModel(
            createGoldPriceAlertUseCase,
            getLatestGoldPriceAlertUseCase,
            fetchBottomSheetStaticDataUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}