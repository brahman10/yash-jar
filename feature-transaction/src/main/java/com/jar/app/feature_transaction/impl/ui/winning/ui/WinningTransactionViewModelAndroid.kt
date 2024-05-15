package com.jar.app.feature_transaction.impl.ui.winning.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_transaction.shared.ui.WinningTransactionViewModel
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchUserWinningDetailsUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchWinningListingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class WinningTransactionViewModelAndroid @Inject constructor(
    private val fetchUserWinningDetailsUseCase: IFetchUserWinningDetailsUseCase,
    private val fetchWinningListingUseCase: IFetchWinningListingUseCase
) : ViewModel() {

    private val viewModel by lazy {
        WinningTransactionViewModel(
            fetchUserWinningDetailsUseCase,
            fetchWinningListingUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}