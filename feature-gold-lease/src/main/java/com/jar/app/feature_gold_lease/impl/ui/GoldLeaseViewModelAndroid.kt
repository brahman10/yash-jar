package com.jar.app.feature_gold_lease.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseViewModel
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
internal class GoldLeaseViewModelAndroid @Inject constructor(
    fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    buyGoldUseCase: BuyGoldUseCase,
    fetchUserGoldBalanceUseCase: FetchUserGoldBalanceUseCase
) : ViewModel() {

    private val viewModel by lazy {
        GoldLeaseViewModel(
            fetchCurrentGoldPriceUseCase,
            buyGoldUseCase,
            fetchUserGoldBalanceUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}