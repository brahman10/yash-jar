package com.jar.app.feature_lending.impl.ui.choose_amount.amount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.api.usecase.FetchLendingV2PreApprovedDataUseCase
import com.jar.app.feature_lending.shared.ui.choose_amount.amount.SelectLoanAmountViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SelectLoanAmountViewModelAndroid @Inject constructor(
    private val fetchPreApprovedDataUseCase: FetchLendingV2PreApprovedDataUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        SelectLoanAmountViewModel(
            fetchPreApprovedDataUseCase = fetchPreApprovedDataUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}