package com.jar.app.feature_lending.impl.ui.agreement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashJourney
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.use_case.FetchReadyCashJourneyUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoanSummaryAndAgreementViewModel @Inject constructor(
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    private val fetchReadyCashJourneyUseCase: FetchReadyCashJourneyUseCase
):ViewModel() {
    private val _updateCheckpointFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>()
    val updateCheckpointFlow: Flow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>
        get() = _updateCheckpointFlow.toCommonFlow()

    private val _readyCashJourneyFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<ReadyCashJourney?>>>()
    val readyCashJourneyFlow: Flow<RestClientResult<ApiResponseWrapper<ReadyCashJourney?>>>
        get() = _readyCashJourneyFlow.toCommonFlow()

    fun fetchLendingProgress() {
        viewModelScope.launch {
            fetchReadyCashJourneyUseCase.getReadyCashJourney().collect {
                _readyCashJourneyFlow.emit(it)
            }
        }
    }

    fun updateCheckpoint(updateLoanDetailsBodyV2: UpdateLoanDetailsBodyV2, checkpoint:String) {
        viewModelScope.launch {
            updateLoanDetailsV2UseCase.updateLoanDetails(
                updateLoanDetailsBodyV2,
                checkpoint
            ).collect {
                _updateCheckpointFlow.emit(it)
            }
        }
    }
}