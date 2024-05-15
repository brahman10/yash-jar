package com.jar.health_insurance.impl.ui.payment_status_page

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.jar.app.core_compose_ui.base.BaseComposeMviViewModel
import com.jar.app.core_compose_ui.base.BaseViewState
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchPaymentStatusUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentStatusViewModel @Inject constructor(
    private val fetchPaymentStatusUseCase: FetchPaymentStatusUseCase
) : BaseComposeMviViewModel<BaseViewState<PaymentStatusState>, PaymentStatusEvent>() {

    override fun onTriggerEvent(eventType: PaymentStatusEvent) {
        when (eventType) {
            is PaymentStatusEvent.fetchPaymentStatus -> fetchPaymentStatus(eventType.insuranceId)
            is PaymentStatusEvent.onContactSupportClicked -> TODO()
            is PaymentStatusEvent.onGoToHomeClicked -> TODO()
            is PaymentStatusEvent.onTopSectionCtaClicked -> TODO()
        }
    }


    private fun fetchPaymentStatus(insuranceId: String) {

        setState(BaseViewState.Loading)
        viewModelScope.launch {
            fetchPaymentStatusUseCase.fetchPaymentStatus(insuranceId)
                .collect(
                    onSuccess = {
                        setState(
                            BaseViewState.Data(
                                PaymentStatusState(
                                    paymentStatus = it
                                )
                            )
                        )
                    },

                    onError = { errorMessage, errorCode ->
                    }
                )
        }
    }
}