package com.jar.health_insurance.impl.ui.manage_insurance_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchInsuranceTransactionDetailsUseCase
import com.jar.app.feature_health_insurance.shared.ui.InsuranceTransactionDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InsuranceTransactionDetailsViewModelAndroid @Inject constructor(private val fetchInsuranceTransactionDetailsUseCase: FetchInsuranceTransactionDetailsUseCase) :
    ViewModel() {
    private val viewModel by lazy {
        InsuranceTransactionDetailsViewModel(
            fetchInsuranceTransactionDetailsUseCase = fetchInsuranceTransactionDetailsUseCase,
            coroutineScope = viewModelScope

        )
    }

    fun getInstance() = viewModel
}