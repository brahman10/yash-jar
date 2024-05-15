package com.jar.app.feature_vasooli.impl.ui.add_repayment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_vasooli.impl.domain.model.PaymentMode
import com.jar.app.feature_vasooli.impl.domain.model.RepaymentEntryRequest
import com.jar.app.feature_vasooli.impl.domain.use_case.PostRepaymentEntryRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AddRepaymentViewModel @Inject constructor(
    private val paymentModeListGenerator: PaymentModeListGenerator,
    private val postRepaymentEntryRequestUseCase: PostRepaymentEntryRequestUseCase
): ViewModel() {

    private val _paymentModeListLiveData = MutableLiveData<List<PaymentMode>>()
    val paymentModeListLiveData: LiveData<List<PaymentMode>>
        get() = _paymentModeListLiveData

    private val _repaymentEntryRequestLiveData = MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val repaymentEntryRequestLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _repaymentEntryRequestLiveData

    private var paymentModeList: List<PaymentMode>? = null

    var selectedPaymentMode: Int? = null

    init {
        getPaymentModeList()
    }

    private fun getPaymentModeList() {
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
}