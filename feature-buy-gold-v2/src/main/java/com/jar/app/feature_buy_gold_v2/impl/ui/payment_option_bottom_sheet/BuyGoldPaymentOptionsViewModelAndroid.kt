package com.jar.app.feature_buy_gold_v2.impl.ui.payment_option_bottom_sheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_buy_gold_v2.shared.ui.BuyGoldPaymentOptionsViewModel
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchEnabledPaymentMethodUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchRecentlyUsedPaymentMethodsUseCase
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class BuyGoldPaymentOptionsViewModelAndroid @Inject constructor(
    private val fetchRecentlyUsedPaymentMethodsUseCase: FetchRecentlyUsedPaymentMethodsUseCase,
    private val fetchEnabledPaymentMethodUseCase: FetchEnabledPaymentMethodUseCase,
    private val analyticsApi: AnalyticsApi
): ViewModel(){

    private val viewModel by lazy {
        BuyGoldPaymentOptionsViewModel(
            fetchRecentlyUsedPaymentMethodsUseCase,
            fetchEnabledPaymentMethodUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun newInstance() = viewModel
}