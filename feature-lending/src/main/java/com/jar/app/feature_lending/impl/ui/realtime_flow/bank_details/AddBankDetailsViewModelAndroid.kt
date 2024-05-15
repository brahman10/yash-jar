package com.jar.app.feature_lending.impl.ui.realtime_flow.bank_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateBankDetailUseCase
import com.jar.app.feature_lending.shared.domain.use_case.ValidateIfscCodeUseCase
import com.jar.app.feature_lending.shared.ui.realtime_flow.bank_details.AddBankDetailsViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AddBankDetailsViewModelAndroid @Inject constructor(
    private val ifscCodeUseCase: ValidateIfscCodeUseCase,
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val updateBankDetailUseCase: UpdateBankDetailUseCase,
    private val analyticsApi: AnalyticsApi
) : ViewModel() {


    private val viewModel by lazy {
        AddBankDetailsViewModel(
            ifscCodeUseCase = ifscCodeUseCase,
            fetchStaticContentUseCase = fetchStaticContentUseCase,
            updateBankDetailUseCase = updateBankDetailUseCase,
            analyticsApi = analyticsApi,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}