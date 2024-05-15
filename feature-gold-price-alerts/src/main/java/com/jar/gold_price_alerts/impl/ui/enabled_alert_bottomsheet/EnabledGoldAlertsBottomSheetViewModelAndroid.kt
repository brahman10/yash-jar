package com.jar.gold_price_alerts.impl.ui.enabled_alert_bottomsheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.feature_gold_price_alerts.shared.domain.use_case.DisableGoldPriceAlertUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.GetLatestGoldPriceAlertUseCase
import com.jar.feature_gold_price_alerts.shared.ui.EnabledGoldAlertsBottomSheetViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class EnabledGoldAlertsBottomSheetViewModelAndroid @Inject constructor(
    private val disableGoldPriceAlertUseCase: DisableGoldPriceAlertUseCase,
    private val fetchLatestGoldPriceAlertUseCase: GetLatestGoldPriceAlertUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        EnabledGoldAlertsBottomSheetViewModel(
            disableGoldPriceAlertUseCase,
            fetchLatestGoldPriceAlertUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}