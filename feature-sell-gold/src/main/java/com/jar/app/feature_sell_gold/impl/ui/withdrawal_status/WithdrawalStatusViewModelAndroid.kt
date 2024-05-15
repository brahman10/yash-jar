package com.jar.app.feature_sell_gold.impl.ui.withdrawal_status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchOrderStatusDynamicCardsUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchSellGoldStaticContentUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchWithdrawalStatusUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IPostTransactionActionUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IPostWithdrawRequestUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IUpdateWithdrawalReasonUseCase
import com.jar.app.feature_sell_gold.shared.ui.withdrawal_status.WithdrawalStatusViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class WithdrawalStatusViewModelAndroid @Inject constructor(
    private val postWithdrawRequestUseCase: IPostWithdrawRequestUseCase,
    private val fetchWithdrawalStatusUseCase: IFetchWithdrawalStatusUseCase,
    private val updateWithdrawalReasonUseCase: IUpdateWithdrawalReasonUseCase,
    private val fetchSellGoldStaticContentUseCase: IFetchSellGoldStaticContentUseCase,
    private val postTransactionActionUseCase: IPostTransactionActionUseCase,
    private val fetchOrderStatusDynamicCardsUseCase: FetchOrderStatusDynamicCardsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        WithdrawalStatusViewModel(
            postWithdrawRequestUseCase,
            fetchWithdrawalStatusUseCase,
            updateWithdrawalReasonUseCase,
            fetchSellGoldStaticContentUseCase,
            postTransactionActionUseCase,
            fetchOrderStatusDynamicCardsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}