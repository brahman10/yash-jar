package com.jar.app.feature_lending.impl.ui.repayments.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.api.usecase.FetchLendingV2PreApprovedDataUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchRepaymentDetailsUseCase
import com.jar.app.feature_lending.shared.domain.use_case.InitiateForeclosurePaymentUseCase
import com.jar.app.feature_lending.shared.ui.repayments.overview.RepaymentOverviewViewModel
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class RepaymentOverviewViewModelAndroid @Inject constructor(
    private val fetchRepaymentDetailsUseCase: FetchRepaymentDetailsUseCase,
    private val initiateForeclosurePaymentUseCase: InitiateForeclosurePaymentUseCase,
    private val fetchLendingV2PreApprovedDataUseCase: FetchLendingV2PreApprovedDataUseCase,
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase
) : ViewModel() {

    private val viewModel by lazy {
        RepaymentOverviewViewModel(
            fetchRepaymentDetailsUseCase = fetchRepaymentDetailsUseCase,
            initiateForeclosurePaymentUseCase = initiateForeclosurePaymentUseCase,
            fetchLendingV2PreApprovedDataUseCase = fetchLendingV2PreApprovedDataUseCase,
            fetchManualPaymentStatusUseCase = fetchManualPaymentStatusUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}