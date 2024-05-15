package com.jar.app.feature_gold_lease.impl.ui.new_lease_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseLandingDetailsUseCase
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseV2NewLeaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject



@HiltViewModel
internal class GoldLeaseV2NewLeaseViewModelAndroid @Inject constructor(
    fetchGoldLeaseLandingDetailsUseCase: FetchGoldLeaseLandingDetailsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        GoldLeaseV2NewLeaseViewModel(
            fetchGoldLeaseLandingDetailsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}