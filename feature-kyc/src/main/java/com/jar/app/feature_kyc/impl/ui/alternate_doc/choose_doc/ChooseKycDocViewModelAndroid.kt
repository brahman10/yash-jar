package com.jar.app.feature_kyc.impl.ui.alternate_doc.choose_doc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycDocumentsListUseCase
import com.jar.app.feature_kyc.shared.ui.alternate_doc.choose_doc.ChooseKycDocViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ChooseKycDocViewModelAndroid @Inject constructor(
    private val fetchKycDocumentsListUseCase: FetchKycDocumentsListUseCase
) : ViewModel() {

    private val viewModel by lazy {
        ChooseKycDocViewModel(
            fetchKycDocumentsListUseCase = fetchKycDocumentsListUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel

}