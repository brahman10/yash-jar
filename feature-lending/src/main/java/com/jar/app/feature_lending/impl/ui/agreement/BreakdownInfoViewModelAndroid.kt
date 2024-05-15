package com.jar.app.feature_lending.impl.ui.agreement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.ui.agreement.BreakdownInfoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class BreakdownInfoViewModelAndroid @Inject constructor(
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        BreakdownInfoViewModel(
            fetchLoanDetailsV2UseCase = fetchLoanDetailsV2UseCase,
            fetchStaticContentUseCase = fetchStaticContentUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}