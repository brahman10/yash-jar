package com.jar.app.feature_lending.impl.ui.bank.penny_drop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchReadyCashJourneyUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.ui.bank.enter_account.BankDetailsViewModel
import com.jar.app.feature_lending.shared.ui.bank.penny_drop.PennyDropViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PennyDropViewModelAndroid @Inject constructor(
    private val fetchReadyCashJourneyUseCase: FetchReadyCashJourneyUseCase,
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
) : ViewModel() {

    private val viewModel by lazy {
        PennyDropViewModel(
            fetchReadyCashJourneyUseCase = fetchReadyCashJourneyUseCase,
            updateLoanDetailsV2UseCase = updateLoanDetailsV2UseCase,
            coroutineScope = viewModelScope
        )
    }
    fun getInstance() = viewModel

}