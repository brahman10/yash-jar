package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.ckyc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchKycAadhaarDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.SearchCkycAadhaarDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.ui.aadhaar.ckyc.CkycFetchViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class CkycFetchViewModelAndroid @Inject constructor(
    private val searchCkycAadhaarDetailsUseCase: SearchCkycAadhaarDetailsUseCase,
    private val fetchAadhaarDetailsUseCase: FetchKycAadhaarDetailsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        CkycFetchViewModel(
            searchCkycAadhaarDetailsUseCase = searchCkycAadhaarDetailsUseCase,
            fetchAadhaarDetailsUseCase = fetchAadhaarDetailsUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}