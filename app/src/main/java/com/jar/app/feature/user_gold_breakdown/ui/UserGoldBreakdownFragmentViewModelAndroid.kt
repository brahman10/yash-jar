package com.jar.app.feature.user_gold_breakdown.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUserGoldBreakdownUseCase
import com.jar.app.feature_homepage.shared.ui.user_gold_breakdown.UserGoldBreakdownFragmentViewModel
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchInvestedAmntBreakupUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class UserGoldBreakdownFragmentViewModelAndroid @Inject constructor(
    private val fetchUserGoldBalanceUseCase: FetchUserGoldBalanceUseCase,
    private val fetchUserGoldBreakdownUseCase: FetchUserGoldBreakdownUseCase,
    private val fetchAmountBreakdownUseCase: IFetchInvestedAmntBreakupUseCase,
): ViewModel() {

    private val viewModel by lazy {
        UserGoldBreakdownFragmentViewModel(
            fetchUserGoldBalanceUseCase,
            fetchUserGoldBreakdownUseCase,
            fetchAmountBreakdownUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}