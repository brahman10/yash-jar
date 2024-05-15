package com.jar.app.feature_gold_lease.impl.ui.my_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseMyOrdersUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchUserLeasesUseCase
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseV2MyOrderViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldLeaseV2MyOrderViewModelAndroid @Inject constructor(
    fetchGoldLeaseMyOrdersUseCase: FetchGoldLeaseMyOrdersUseCase,
    fetchUserLeasesUseCase: FetchUserLeasesUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        GoldLeaseV2MyOrderViewModel(
            fetchGoldLeaseMyOrdersUseCase,
            fetchUserLeasesUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}