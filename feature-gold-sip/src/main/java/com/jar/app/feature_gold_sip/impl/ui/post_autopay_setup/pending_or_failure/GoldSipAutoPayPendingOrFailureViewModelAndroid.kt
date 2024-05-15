package com.jar.app.feature_gold_sip.impl.ui.post_autopay_setup.pending_or_failure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase
import com.jar.app.feature_gold_sip.shared.ui.GoldSipAutoPayPendingOrFailureViewModel
import com.jar.app.feature_mandate_payments_common.shared.domain.use_case.FetchMandatePaymentStatusUseCase
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldSipAutoPayPendingOrFailureViewModelAndroid @Inject constructor(
    private val updateGoldSipDetailsUseCase: UpdateGoldSipDetailsUseCase,
    private val fetchMandatePaymentStatusUseCase: FetchMandatePaymentStatusUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        GoldSipAutoPayPendingOrFailureViewModel(
            updateGoldSipDetailsUseCase,
            fetchMandatePaymentStatusUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}