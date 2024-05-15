package com.jar.app.feature_kyc.impl.ui.upload_doc_failed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_kyc.shared.api.use_case.PostKycOcrRequestUseCase
import com.jar.app.feature_kyc.shared.ui.upload_doc_failed.UploadKycDocFailedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class UploadKycDocFailedViewModelAndroid @Inject constructor(
    private val postKycOcrRequestUseCase: PostKycOcrRequestUseCase
) : ViewModel() {

    private val viewModel by lazy {
        UploadKycDocFailedViewModel(
            postKycOcrRequestUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}