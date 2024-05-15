package com.jar.app.feature_gold_lease.impl.ui.order_summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseOrderSummaryUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseRetryDataUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.InitiateGoldLeaseV2UseCase
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseV2OrderSummaryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldLeaseV2OrderSummaryViewModelAndroid @Inject constructor(
    fetchGoldLeaseOrderSummaryUseCase: FetchGoldLeaseOrderSummaryUseCase,
    initiateGoldLeaseV2UseCase: InitiateGoldLeaseV2UseCase,
    fetchGoldLeaseRetryDataUseCase: FetchGoldLeaseRetryDataUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        GoldLeaseV2OrderSummaryViewModel(
            fetchGoldLeaseOrderSummaryUseCase,
            initiateGoldLeaseV2UseCase,
            fetchGoldLeaseRetryDataUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}