package com.jar.app.feature_lending.impl.ui.bank.confirm_bank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.ui.bank.confirm_bank.ConfirmBankDetailViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ConfirmBankDetailViewModelAndroid @Inject constructor(
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
) : ViewModel() {


    private val viewModel by lazy {
        ConfirmBankDetailViewModel(
            fetchStaticContentUseCase,
            fetchLoanDetailsV2UseCase,
            updateLoanDetailsV2UseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}