package com.jar.app.feature_sell_gold.impl.ui.vpa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_gold_price.shared.data.GoldPriceFlow
import com.jar.app.feature_sell_gold.shared.ui.vpa.VpaSelectionViewModel
import com.jar.app.feature_settings.domain.use_case.AddNewUpiIdUseCase
import com.jar.app.feature_settings.domain.use_case.FetchVpaChipUseCase
import com.jar.app.feature_settings.domain.use_case.VerifyUpiUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserVpaUseCase
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VpaSelectionViewModelAndroid @Inject constructor(
    goldPriceFlow: GoldPriceFlow,
    fetchUserSavedVpaUseCase: FetchUserVpaUseCase,
    fetchVpaChipUseCase: FetchVpaChipUseCase,
    verifyUpiUseCase: VerifyUpiUseCase,
    addNewUpiIdUseCase: AddNewUpiIdUseCase,
    buyGoldUseCase: BuyGoldUseCase,
    analyticsApi: AnalyticsApi
) : ViewModel() {
    private val viewModel by lazy {
        VpaSelectionViewModel(
            goldPriceFlow,
            fetchUserSavedVpaUseCase,
            fetchVpaChipUseCase,
            verifyUpiUseCase,
            addNewUpiIdUseCase,
            buyGoldUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}