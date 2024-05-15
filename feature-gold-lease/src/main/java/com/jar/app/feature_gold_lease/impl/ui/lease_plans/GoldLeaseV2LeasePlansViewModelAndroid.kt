package com.jar.app.feature_gold_lease.impl.ui.lease_plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseJewellerListingsUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeasePlanFiltersUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeasePlansUseCase
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseV2LeasePlansViewModel
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldLeaseV2LeasePlansViewModelAndroid @Inject constructor(
    fetchGoldLeaseJewellerListingsUseCase: FetchGoldLeaseJewellerListingsUseCase,
    fetchUserGoldBalanceUseCase: FetchUserGoldBalanceUseCase,
    fetchGoldLeasePlanFiltersUseCase: FetchGoldLeasePlanFiltersUseCase,
    fetchGoldLeasePlansUseCase: FetchGoldLeasePlansUseCase
) : ViewModel() {

    private val viewModel by lazy {
        GoldLeaseV2LeasePlansViewModel(
            fetchGoldLeaseJewellerListingsUseCase,
            fetchUserGoldBalanceUseCase,
            fetchGoldLeasePlanFiltersUseCase,
            fetchGoldLeasePlansUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}