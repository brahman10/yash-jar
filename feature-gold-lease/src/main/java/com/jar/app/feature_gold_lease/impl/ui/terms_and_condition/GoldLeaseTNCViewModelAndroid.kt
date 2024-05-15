package com.jar.app.feature_gold_lease.impl.ui.terms_and_condition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseTermsAndConditionsUseCase

import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseTNCViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldLeaseTNCViewModelAndroid @Inject constructor(
    fetchGoldLeaseTermsAndConditionsUseCase: FetchGoldLeaseTermsAndConditionsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        GoldLeaseTNCViewModel(
            fetchGoldLeaseTermsAndConditionsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}