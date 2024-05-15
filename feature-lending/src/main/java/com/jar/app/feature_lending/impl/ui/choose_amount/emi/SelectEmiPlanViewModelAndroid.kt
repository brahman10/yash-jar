package com.jar.app.feature_lending.impl.ui.choose_amount.emi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.feature_lending.shared.domain.use_case.FetchEmiPlansUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.ui.choose_amount.emi.SelectEmiPlanViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SelectEmiPlanViewModelAndroid @Inject constructor(
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    private val fetchEmiPlansUseCase: FetchEmiPlansUseCase,
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {


    private val viewModel by lazy {
        SelectEmiPlanViewModel(
            updateLoanDetailsV2UseCase = updateLoanDetailsV2UseCase,
            fetchEmiPlansUseCase = fetchEmiPlansUseCase,
            fetchLoanDetailsV2UseCase = fetchLoanDetailsV2UseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}