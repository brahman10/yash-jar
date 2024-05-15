package com.jar.app.feature_lending.impl.ui.foreclosure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.InitiateForeclosurePaymentUseCase
import com.jar.app.feature_lending.shared.ui.foreclosure.ForeclosureSummaryViewModel
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ForeclosureSummaryViewModelAndroid @Inject constructor(
    private val initiateForeclosurePaymentUseCase: InitiateForeclosurePaymentUseCase,
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    private val networkFlow: NetworkFlow,
) : ViewModel() {

    private val _networkStateFlow = MutableStateFlow<Boolean>(false)
    val networkStateFlow: CFlow<Boolean>
        get() = _networkStateFlow.toCommonFlow()

    var loanDetailsV2: LoanDetailsV2? = null

    private var networkJob: Job? = null

    fun observeNetwork() {
        networkJob?.cancel()
        networkJob = viewModelScope.launch {
            networkFlow.networkStatus.collect {
                _networkStateFlow.emit(it)
            }
        }
    }

    private val viewModel by lazy {
        ForeclosureSummaryViewModel(
            initiateForeclosurePaymentUseCase = initiateForeclosurePaymentUseCase,
            fetchLoanDetailsV2UseCase = fetchLoanDetailsV2UseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}