package com.jar.app.feature_gold_sip.impl.ui.mandate_redirection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase
import com.jar.app.feature_gold_sip.shared.ui.SipMandateRedirectionViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SipMandateRedirectionViewModelAndroid @Inject constructor(
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val updateGoldSipDetailsUseCase: UpdateGoldSipDetailsUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        SipMandateRedirectionViewModel(
            fetchCurrentGoldPriceUseCase,
            updateGoldSipDetailsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}