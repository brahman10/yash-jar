package com.jar.app.feature_vasooli.impl.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_vasooli.impl.domain.model.Borrower
import com.jar.app.feature_vasooli.impl.domain.model.Reminder
import com.jar.app.feature_vasooli.impl.domain.model.Repayment
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchLoanDetailsUseCase
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchReminderUseCase
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchRepaymentHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class VasooliDetailsViewModel @Inject constructor(
    private val fetchRepaymentHistoryUseCase: FetchRepaymentHistoryUseCase,
    private val fetchLoanDetailsUseCase: FetchLoanDetailsUseCase,
    private val fetchReminderUseCase: FetchReminderUseCase
): ViewModel(){

    private val _repaymentHistoryLiveData = SingleLiveEvent<RestClientResult<ApiResponseWrapper<List<Repayment>>>>()
    val repaymentHistoryLiveData: LiveData<RestClientResult<ApiResponseWrapper<List<Repayment>>>>
        get() = _repaymentHistoryLiveData

    private val _loanDetailsLiveData = SingleLiveEvent<RestClientResult<ApiResponseWrapper<Borrower>>>()
    val loanDetailsLiveData: LiveData<RestClientResult<ApiResponseWrapper<Borrower>>>
        get() = _loanDetailsLiveData

    private val _reminderLiveData = SingleLiveEvent<RestClientResult<ApiResponseWrapper<Reminder>>>()
    val reminderLiveData: LiveData<RestClientResult<ApiResponseWrapper<Reminder>>>
        get() = _reminderLiveData

    fun fetchRepaymentHistory(loanId: String) {
        viewModelScope.launch {
            fetchRepaymentHistoryUseCase.fetchRepaymentHistory(loanId).collect {
                _repaymentHistoryLiveData.postValue(it)
            }
        }
    }

    fun fetchLoanDetails(loanId: String) {
        viewModelScope.launch {
            fetchLoanDetailsUseCase.fetchLoanDetails(loanId).collect {
                _loanDetailsLiveData.postValue(it)
            }
        }
    }

    fun fetchReminder(loanId: String, medium: String) {
        viewModelScope.launch {
            fetchReminderUseCase.fetchReminder(loanId, medium).collect {
                _reminderLiveData.postValue(it)
            }
        }
    }
}