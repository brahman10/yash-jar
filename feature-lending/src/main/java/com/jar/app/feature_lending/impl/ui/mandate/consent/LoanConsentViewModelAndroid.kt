package com.jar.app.feature_lending.impl.ui.mandate.consent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.ui.mandate.consent.LoanConsentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LoanConsentViewModelAndroid @Inject constructor(
    private val staticContentUseCase: FetchStaticContentUseCase,
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
) : ViewModel() {

    private val viewModel by lazy {
        LoanConsentViewModel(
            staticContentUseCase = staticContentUseCase,
            updateLoanDetailsV2UseCase = updateLoanDetailsV2UseCase,
            fetchLoanDetailsV2UseCase = fetchLoanDetailsV2UseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}