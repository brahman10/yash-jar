package com.jar.app.feature_promo_code.impl.ui.promo_transaction_status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jar.app.feature_promo_code.shared.domain.use_cases.FetchPromoCodeTransactionStatusUseCase
import com.jar.app.feature_promo_code.shared.ui.PromoCodeStatusViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PromoCodeStatusViewModelAndroid @Inject constructor(
    fetchPromoCodeTransactionStatusUseCase: FetchPromoCodeTransactionStatusUseCase,
    analyticsApi: AnalyticsApi
): ViewModel() {

    private val viewModel by lazy {
        PromoCodeStatusViewModel(
            fetchPromoCodeTransactionStatusUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}