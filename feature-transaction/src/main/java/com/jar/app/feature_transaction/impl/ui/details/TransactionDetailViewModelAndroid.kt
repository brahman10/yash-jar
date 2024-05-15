package com.jar.app.feature_transaction.impl.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchTxnDetailsUseCase
import com.jar.app.feature_transaction.shared.ui.TransactionDetailViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class TransactionDetailViewModelAndroid @Inject constructor(
    private val fetchTxnDetailsUseCase: IFetchTxnDetailsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        TransactionDetailViewModel(
            fetchTxnDetailsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}