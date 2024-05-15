package com.jar.app.feature_lending_kyc.impl.ui.capture_photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.DispatcherProvider
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_kyc.shared.api.use_case.PostKycOcrRequestUseCase
import com.jar.app.feature_kyc.shared.domain.model.KycOcrResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
internal class CaptureDocumentPhotoViewModel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val postDocumentOcrRequestUseCase: PostKycOcrRequestUseCase
) : ViewModel() {

    private val _documentOcrRequestLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<KycOcrResponse?>>>()
    val documentOcrRequestLiveData: LiveData<RestClientResult<ApiResponseWrapper<KycOcrResponse?>>>
        get() = _documentOcrRequestLiveData


    private var job: Job? = null

    fun postDocumentOcrRequest(docType: String, filePath: String) {
        job?.cancel()
        job = viewModelScope.launch(dispatcherProvider.io) {
            postDocumentOcrRequestUseCase.postKycOcrRequest(
                docType,
                File(filePath).readBytes(),
                true
            ).collect {
                _documentOcrRequestLiveData.postValue(it)
            }
        }
    }
}