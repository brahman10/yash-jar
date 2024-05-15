package com.jar.app.feature_transaction.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchTransactionFilterUseCase
import com.jar.app.feature_transaction.shared.ui.TransactionFragmentViewModel
import com.jar.app.feature_user_api.domain.use_case.FetchUserKycStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionFragmentViewModelAndroid @Inject constructor(
    private val fetchTransactionFilterUseCase: IFetchTransactionFilterUseCase,
    private val fetchUserKycStatusUseCase: FetchUserKycStatusUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        TransactionFragmentViewModel(
            fetchTransactionFilterUseCase,
            fetchUserKycStatusUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}