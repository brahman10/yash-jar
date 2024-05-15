package com.jar.app.feature_gold_lease.impl.ui.jeweller_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseJewellerDetailsUseCase
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseV2JewellerDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldLeaseV2JewellerDetailsViewModelAndroid @Inject constructor(
    fetchGoldLeaseJewellerDetailsUseCase: FetchGoldLeaseJewellerDetailsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        GoldLeaseV2JewellerDetailsViewModel(
            fetchGoldLeaseJewellerDetailsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}