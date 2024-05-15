package com.jar.app.feature_lending.impl.ui.foreclosure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.ui.foreclosure.ForeclosureStatusViewModel
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ForeclosureStatusViewModelAndroid @Inject constructor(
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase
) : ViewModel() {

    private val viewModel by lazy {
        ForeclosureStatusViewModel(
            fetchManualPaymentStatusUseCase = fetchManualPaymentStatusUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}