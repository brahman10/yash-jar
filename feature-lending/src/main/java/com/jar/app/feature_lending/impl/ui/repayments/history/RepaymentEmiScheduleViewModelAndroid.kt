package com.jar.app.feature_lending.impl.ui.repayments.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchEmiTxnHistoryUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.ui.repayments.history.RepaymentEmiScheduleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class RepaymentEmiScheduleViewModelAndroid @Inject constructor(
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val fetchEmiTxnHistoryUseCase: FetchEmiTxnHistoryUseCase
) : ViewModel() {

    private val viewModel by lazy {
        RepaymentEmiScheduleViewModel(
            fetchStaticContentUseCase = fetchStaticContentUseCase,
            fetchEmiTxnHistoryUseCase = fetchEmiTxnHistoryUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}