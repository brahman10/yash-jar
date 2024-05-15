package com.jar.app.feature_spends_tracker.impl.ui.report_transaction_bs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_spends_tracker.shared.domain.model.report_transaction.ReportTransactionRequest
import com.jar.app.feature_spends_tracker.shared.domain.usecase.ReportTransactionUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ReportTransactionViewModel @Inject constructor(
    private val reportTransactionUseCase: ReportTransactionUseCase
) : ViewModel() {
    private val _reportTransactionLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val reportTransactionLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _reportTransactionLiveData

    fun reportTransaction(transactionId: String, reportReason: String) {
        viewModelScope.launch {
            reportTransactionUseCase.reportTransaction(
                ReportTransactionRequest(
                    transactionId,
                    reportReason
                )
            ).collect {

                _reportTransactionLiveData.postValue(it)
            }
        }
    }
}