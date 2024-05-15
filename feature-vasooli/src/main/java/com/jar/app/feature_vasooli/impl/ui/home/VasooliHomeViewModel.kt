package com.jar.app.feature_vasooli.impl.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_vasooli.impl.domain.model.Borrower
import com.jar.app.feature_vasooli.impl.domain.model.VasooliOverview
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchLoansListUseCase
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchVasooliOverviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class VasooliHomeViewModel @Inject constructor(
    private val fetchVasooliOverviewUseCase: FetchVasooliOverviewUseCase,
    private val fetchLoansListUseCase: FetchLoansListUseCase
) : ViewModel() {

    private val _vasooliOverviewLiveData = SingleLiveEvent<RestClientResult<ApiResponseWrapper<VasooliOverview>>>()
    val vasooliOverviewLiveData: LiveData<RestClientResult<ApiResponseWrapper<VasooliOverview>>>
        get() = _vasooliOverviewLiveData

    private val _loansListLiveData = SingleLiveEvent<RestClientResult<ApiResponseWrapper<List<Borrower>>>>()
    val loansListLiveData: LiveData<RestClientResult<ApiResponseWrapper<List<Borrower>>>>
        get() = _loansListLiveData

    fun fetchVasooliOverview() {
        viewModelScope.launch {
            fetchVasooliOverviewUseCase.fetchVasooliOverview().collect {
                _vasooliOverviewLiveData.postValue(it)
            }
        }
    }

    fun fetchLoansList() {
        viewModelScope.launch {
            fetchLoansListUseCase.fetchLoansList().collect {
                _loansListLiveData.postValue(it)
            }
        }
    }
}