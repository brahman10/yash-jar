package com.jar.app.feature_lending.impl.ui.bank.enter_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.ValidateIfscCodeUseCase
import com.jar.app.feature_lending.shared.ui.bank.enter_account.BankDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class BankDetailsViewModelAndroid @Inject constructor(
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    private val ifscCodeUseCase: ValidateIfscCodeUseCase
) : ViewModel() {

    private val viewModel by lazy {
        BankDetailsViewModel(
            updateLoanDetailsV2UseCase = updateLoanDetailsV2UseCase,
            ifscCodeUseCase = ifscCodeUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel

}