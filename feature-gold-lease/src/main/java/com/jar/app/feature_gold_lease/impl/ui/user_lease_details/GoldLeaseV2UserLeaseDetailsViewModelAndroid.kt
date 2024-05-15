package com.jar.app.feature_gold_lease.impl.ui.user_lease_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseV2TransactionsUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchUserLeaseDetailsUseCase
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseV2UserLeaseDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldLeaseV2UserLeaseDetailsViewModelAndroid @Inject constructor(
    fetchUserLeaseDetailsUseCase: FetchUserLeaseDetailsUseCase,
    fetchGoldLeaseV2TransactionsUseCase: FetchGoldLeaseV2TransactionsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        GoldLeaseV2UserLeaseDetailsViewModel(
            fetchUserLeaseDetailsUseCase,
            fetchGoldLeaseV2TransactionsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}