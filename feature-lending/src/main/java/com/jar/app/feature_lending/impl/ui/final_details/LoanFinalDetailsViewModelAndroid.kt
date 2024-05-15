package com.jar.app.feature_lending.impl.ui.final_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.ui.final_details.LoanFinalDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LoanFinalDetailsViewModelAndroid @Inject constructor(
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
) : ViewModel() {

    private val viewModel by lazy {
        LoanFinalDetailsViewModel(
            fetchLoanDetailsV2UseCase = fetchLoanDetailsV2UseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}