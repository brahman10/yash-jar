package com.jar.app.feature_kyc.impl.ui.alternate_doc.upload_doc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_kyc.shared.api.use_case.PostKycOcrRequestUseCase
import com.jar.app.feature_kyc.shared.ui.alternate_doc.choose_doc.UploadKycDocViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class UploadKycDocViewModelAndroid @Inject constructor(
    private val postKycOcrRequestUseCase: PostKycOcrRequestUseCase
) : ViewModel() {

    private val viewModel by lazy {
        UploadKycDocViewModel(
            postKycOcrRequestUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}