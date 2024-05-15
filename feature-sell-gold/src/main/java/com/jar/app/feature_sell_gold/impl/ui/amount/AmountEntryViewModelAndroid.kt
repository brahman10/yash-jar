package com.jar.app.feature_sell_gold.impl.ui.amount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_gold_price.shared.data.GoldPriceFlow
import com.jar.app.feature_sell_gold.shared.domain.use_cases.FetchDrawerDetailsUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.FetchKycDetailsForSellGoldUseCase
import com.jar.app.feature_sell_gold.shared.ui.amount.AmountEntryViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AmountEntryViewModelAndroid @Inject constructor(
    goldPriceFlow: GoldPriceFlow,
    fetchDrawerDetailsUseCase: FetchDrawerDetailsUseCase,
    fetchKycDetailsForSellGoldUseCase: FetchKycDetailsForSellGoldUseCase,
    buyGoldUseCase: BuyGoldUseCase,
    analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        AmountEntryViewModel(
            goldPriceFlow,
            fetchDrawerDetailsUseCase,
            fetchKycDetailsForSellGoldUseCase,
            buyGoldUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}