package com.jar.app.feature_round_off.impl.ui.manual_confirmation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.domain.model.OneTimePaymentGateway
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest
import com.jar.app.feature_round_off.shared.domain.use_case.InitiateDetectedSpendPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManualRoundOffConfirmationViewModel @Inject constructor(
    private val initiateDetectedSpendPaymentUseCase: InitiateDetectedSpendPaymentUseCase,
) : ViewModel() {

    private val _initiateDetectedSpendPaymentLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>()
    val initiateDetectedSpendPaymentLiveData: LiveData<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
        get() = _initiateDetectedSpendPaymentLiveData


    /************ Manual Payment ************/
    fun initiateDetectedSpendPayment(
        initiateDetectedRoundOffsPaymentRequest: com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest,
        paymentGateway: OneTimePaymentGateway
    ) {
        viewModelScope.launch {
            initiateDetectedSpendPaymentUseCase.makeDetectedSpendsPayment(
                initiateDetectedRoundOffsPaymentRequest,
                paymentGateway
            ).collect {
                _initiateDetectedSpendPaymentLiveData.postValue(it)
            }
        }
    }
}