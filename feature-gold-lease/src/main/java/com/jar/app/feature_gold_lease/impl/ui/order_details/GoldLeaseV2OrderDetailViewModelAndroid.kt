package com.jar.app.feature_gold_lease.impl.ui.order_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseGoldOptionsUseCase
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseV2OrderDetailViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldLeaseV2OrderDetailViewModelAndroid @Inject constructor(
    fetchGoldLeaseGoldOptionsUseCase: FetchGoldLeaseGoldOptionsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        GoldLeaseV2OrderDetailViewModel(
            fetchGoldLeaseGoldOptionsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}