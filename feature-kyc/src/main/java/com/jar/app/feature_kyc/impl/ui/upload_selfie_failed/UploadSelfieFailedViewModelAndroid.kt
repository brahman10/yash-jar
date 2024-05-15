package com.jar.app.feature_kyc.impl.ui.upload_selfie_failed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_kyc.shared.api.use_case.PostFaceMatchRequestUseCase
import com.jar.app.feature_kyc.shared.ui.upload_selfie_failed.UploadSelfieFailedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class UploadSelfieFailedViewModelAndroid @Inject constructor(
    private val postFaceMatchRequestUseCase: PostFaceMatchRequestUseCase
) : ViewModel() {

    private val viewModel by lazy {
        UploadSelfieFailedViewModel(
            postFaceMatchRequestUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}