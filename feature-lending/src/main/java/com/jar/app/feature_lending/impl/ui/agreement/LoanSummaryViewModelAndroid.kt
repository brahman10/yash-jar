package com.jar.app.feature_lending.impl.ui.agreement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchLendingAgreementUseCase
import com.jar.app.feature_lending.shared.ui.agreement.LoanSummaryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LoanSummaryViewModelAndroid @Inject constructor(
    private val lendingAgreementUseCase: FetchLendingAgreementUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        LoanSummaryViewModel(
            lendingAgreementUseCase = lendingAgreementUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}