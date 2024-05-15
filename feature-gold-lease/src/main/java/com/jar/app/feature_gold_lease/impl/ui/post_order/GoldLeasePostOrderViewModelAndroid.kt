package com.jar.app.feature_gold_lease.impl.ui.post_order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseStatusUseCase
import com.jar.app.feature_gold_lease.shared.ui.GoldLeasePostOrderViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldLeasePostOrderViewModelAndroid @Inject constructor(
    fetchGoldLeaseStatusUseCase: FetchGoldLeaseStatusUseCase
) : ViewModel() {

    private val viewModel by lazy {
        GoldLeasePostOrderViewModel(
            fetchGoldLeaseStatusUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}