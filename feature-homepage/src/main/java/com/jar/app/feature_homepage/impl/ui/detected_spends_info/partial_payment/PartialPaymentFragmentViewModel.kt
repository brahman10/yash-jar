package com.jar.app.feature_homepage.impl.ui.detected_spends_info.partial_payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.app.feature_round_off.shared.domain.use_case.InitiateDetectedSpendPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PartialPaymentFragmentViewModel @Inject constructor(
    private val initiateDetectedSpendPaymentUseCase: InitiateDetectedSpendPaymentUseCase
) : ViewModel() {

    private val _skipPaymentLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val skipPaymentLiveData: LiveData<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _skipPaymentLiveData

    fun skipPayment(txnAmount: Float, orderId: String) {
        viewModelScope.launch {
            initiateDetectedSpendPaymentUseCase.makeDetectedSpendsPayment(
                com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest(
                    txnAmt = txnAmount,
                    orderId = orderId,
                    skip = true
                ), OneTimePaymentGateway.PAYTM //pass anything as this is skip case. no payment is involved..
            ).collect {
                _skipPaymentLiveData.postValue(it)
            }
        }
    }
}