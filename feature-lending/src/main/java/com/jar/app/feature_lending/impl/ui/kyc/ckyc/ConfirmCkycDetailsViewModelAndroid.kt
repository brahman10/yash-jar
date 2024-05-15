package com.jar.app.feature_lending.impl.ui.kyc.ckyc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.ui.kyc.ckyc.ConfirmCkycViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ConfirmCkycDetailsViewModelAndroid @Inject constructor(
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase
):ViewModel() {

    private val viewModel by lazy {
        ConfirmCkycViewModel(
            fetchLoanDetailsV2UseCase = fetchLoanDetailsV2UseCase,
            updateLoanDetailsV2UseCase = updateLoanDetailsV2UseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}