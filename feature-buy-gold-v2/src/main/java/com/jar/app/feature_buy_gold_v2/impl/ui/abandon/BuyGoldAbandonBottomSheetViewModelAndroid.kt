package com.jar.app.feature_buy_gold_v2.impl.ui.abandon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchBuyGoldAbandonDataUseCase
import com.jar.app.feature_buy_gold_v2.shared.ui.BuyGoldAbandonBottomSheetViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class BuyGoldAbandonBottomSheetViewModelAndroid @Inject constructor(
    private val fetchBuyGoldAbandonDataUseCase: FetchBuyGoldAbandonDataUseCase,
    private val analyticsApi: AnalyticsApi,
) : ViewModel() {

    private val viewModel by lazy {
        BuyGoldAbandonBottomSheetViewModel(
            fetchBuyGoldAbandonDataUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}