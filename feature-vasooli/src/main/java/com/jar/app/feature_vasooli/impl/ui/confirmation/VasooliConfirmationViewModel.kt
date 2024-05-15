package com.jar.app.feature_vasooli.impl.ui.confirmation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_vasooli.impl.domain.model.*
import com.jar.app.feature_vasooli.impl.domain.use_case.DeleteVasooliEntryUseCase
import com.jar.app.feature_vasooli.impl.domain.use_case.PostRepaymentEntryRequestUseCase
import com.jar.app.feature_vasooli.impl.domain.use_case.UpdateVasooliStatusUseCase
import com.jar.app.feature_vasooli.impl.ui.add_repayment.PaymentModeListGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class VasooliConfirmationViewModel @Inject constructor(
    private val paymentModeListGenerator: PaymentModeListGenerator,
    private val deleteVasooliEntryUseCase: DeleteVasooliEntryUseCase,
    private val updateVasooliStatusUseCase: UpdateVasooliStatusUseCase,
    private val postRepaymentEntryRequestUseCase: PostRepaymentEntryRequestUseCase
): ViewModel() {

    private val _paymentModeListLiveData = MutableLiveData<List<PaymentMode>>()
    val paymentModeListLiveData: LiveData<List<PaymentMode>>
        get() = _paymentModeListLiveData

    private val _repaymentEntryRequestLiveData = MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val repaymentEntryRequestLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _repaymentEntryRequestLiveData

    private val _deleteVasooliEntryLiveData = MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val deleteVasooliEntryLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _deleteVasooliEntryLiveData

    private val _updateVasooliStatusLiveData = MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val updateVasooliStatusLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _updateVasooliStatusLiveData

    private var paymentModeList: List<PaymentMode>? = null

    var selectedPaymentMode: Int? = null

    fun getPaymentModeList() {
        viewModelScope.launch {
            paymentModeList = paymentModeListGenerator.getPaymentModeList()
            _paymentModeListLiveData.postValue(paymentModeList.orEmpty())
        }
    }

    fun updateSelectedPaymentMode(paymentMode: PaymentMode) {
        selectedPaymentMode = paymentMode.title
        viewModelScope.launch {
            val newList = paymentModeList?.map {
                if (it.id == paymentMode.id) {
                    it.copy(isSelected = true)
                } else {
                    it.copy(isSelected = false)
                }
            }.orEmpty()
            _paymentModeListLiveData.postValue(newList)
        }
    }

    fun postRepaymentEntryRequest(repaymentEntryRequest: RepaymentEntryRequest) {
        viewModelScope.launch {
            postRepaymentEntryRequestUseCase.postRepaymentEntryRequest(repaymentEntryRequest).collect {
                _repaymentEntryRequestLiveData.postValue(it)
            }
        }
    }

    
    fun updateVasooliStatus(updateStatusRequest: UpdateStatusRequest) {
        viewModelScope.launch {
            updateVasooliStatusUseCase.updateVasooliStatus(updateStatusRequest).collect {
                _updateVasooliStatusLiveData.postValue(it)
            }
        }
    }

    fun deleteVasooliEntry(loanId: String) {
        viewModelScope.launch {
            deleteVasooliEntryUseCase.deleteVasooliEntry(loanId).collect {
                _deleteVasooliEntryLiveData.postValue(it)
            }
        }
    }
}