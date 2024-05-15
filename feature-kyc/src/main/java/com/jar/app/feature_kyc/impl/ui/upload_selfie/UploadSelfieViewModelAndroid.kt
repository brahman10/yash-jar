package com.jar.app.feature_kyc.impl.ui.upload_selfie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_kyc.shared.api.use_case.PostFaceMatchRequestUseCase
import com.jar.app.feature_kyc.shared.ui.upload_selfie.UploadSelfieViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class UploadSelfieViewModelAndroid @Inject constructor(
    private val postFaceMatchRequestUseCase: PostFaceMatchRequestUseCase
) : ViewModel() {

    private val viewModel by lazy {
        UploadSelfieViewModel(
            postFaceMatchRequestUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}