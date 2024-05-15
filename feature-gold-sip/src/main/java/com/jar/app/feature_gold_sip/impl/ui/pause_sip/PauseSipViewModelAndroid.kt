package com.jar.app.feature_gold_sip.impl.ui.pause_sip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_sip.shared.ui.PauseSipViewModel
import com.jar.app.feature_user_api.domain.use_case.UpdatePauseSavingUseCase
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PauseSipViewModelAndroid @Inject constructor(
    private val updatePauseSavingUseCase: UpdatePauseSavingUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        PauseSipViewModel(
            updatePauseSavingUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}