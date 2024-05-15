package com.jar.app.feature_transaction.impl.ui.gold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchTransactionListingUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchUserGoldDetailsUseCase
import com.jar.app.feature_transaction.shared.ui.GoldTransactionViewModel
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldTransactionViewModelAndroid @Inject constructor(
    private val fetchUserGoldDetailsUseCase: IFetchUserGoldDetailsUseCase,
    private val fetchTransactionListingUseCase: IFetchTransactionListingUseCase,
    private val fetchUserGoldBalanceUseCase: FetchUserGoldBalanceUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        GoldTransactionViewModel(
            fetchUserGoldDetailsUseCase,
            fetchTransactionListingUseCase,
            fetchUserGoldBalanceUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}