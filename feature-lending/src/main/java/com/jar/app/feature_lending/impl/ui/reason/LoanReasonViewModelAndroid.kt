package com.jar.app.feature_lending.impl.ui.reason

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.ui.host_container.LendingHostViewModel
import com.jar.app.feature_lending.shared.ui.reason.LoanReasonViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LoanReasonViewModelAndroid @Inject constructor(
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
) : ViewModel() {

    private val viewModel by lazy {
        LoanReasonViewModel(
            updateLoanDetailsV2UseCase = updateLoanDetailsV2UseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel

}