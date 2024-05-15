package com.jar.app.feature_gold_sip.impl.ui.sip_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase
import com.jar.app.feature_gold_sip.shared.ui.GoldSipDetailsViewModel
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdatePauseSavingUseCase
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldSipDetailsViewModelAndroid @Inject constructor(
    private val updateGoldSipDetailsUseCase: UpdateGoldSipDetailsUseCase,
    private val fetchGoldSipDetailsUseCase: FetchGoldSipDetailsUseCase,
    private val updatePauseSavingUseCase: UpdatePauseSavingUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {

    private val viewModel by lazy {
        GoldSipDetailsViewModel(
            updateGoldSipDetailsUseCase,
            fetchGoldSipDetailsUseCase,
            updatePauseSavingUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}