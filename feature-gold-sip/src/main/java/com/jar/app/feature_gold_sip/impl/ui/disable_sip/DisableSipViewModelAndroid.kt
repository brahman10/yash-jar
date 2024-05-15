package com.jar.app.feature_gold_sip.impl.ui.disable_sip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_sip.shared.domain.use_case.DisableGoldSipUseCase
import com.jar.app.feature_gold_sip.shared.ui.DisableSipViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class DisableSipViewModelAndroid @Inject constructor(
    private val disableGoldSipUseCase: DisableGoldSipUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        DisableSipViewModel(disableGoldSipUseCase, analyticsApi, viewModelScope)
    }

    fun getInstance() = viewModel
}