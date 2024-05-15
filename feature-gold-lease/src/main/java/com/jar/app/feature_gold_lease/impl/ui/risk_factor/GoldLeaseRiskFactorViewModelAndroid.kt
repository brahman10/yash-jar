package com.jar.app.feature_gold_lease.impl.ui.risk_factor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseRiskFactorUseCase
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseRiskFactorViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldLeaseRiskFactorViewModelAndroid @Inject constructor(
    fetchGoldLeaseRiskFactorUseCase: FetchGoldLeaseRiskFactorUseCase
) : ViewModel() {

    private val viewModel by lazy {
        GoldLeaseRiskFactorViewModel(
            fetchGoldLeaseRiskFactorUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}