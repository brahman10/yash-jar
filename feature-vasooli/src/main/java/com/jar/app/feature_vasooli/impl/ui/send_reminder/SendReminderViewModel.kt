package com.jar.app.feature_vasooli.impl.ui.send_reminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_vasooli.impl.domain.model.Reminder
import com.jar.app.feature_vasooli.impl.domain.model.SendReminderRequest
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchNewImageUseCase
import com.jar.app.feature_vasooli.impl.domain.use_case.PostSendReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SendReminderViewModel @Inject constructor(
    private val fetchNewImageUseCase: FetchNewImageUseCase,
    private val postSendReminderUseCase: PostSendReminderUseCase
) : ViewModel() {

    private val _newImageLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Reminder>>>()
    val newImageLiveData: LiveData<RestClientResult<ApiResponseWrapper<Reminder>>>
        get() = _newImageLiveData

    private val _sendReminderLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val sendReminderLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _sendReminderLiveData

    fun fetchNewImage(ignoreIndex: String) {
        viewModelScope.launch {
            fetchNewImageUseCase.fetchNewImage(ignoreIndex).collect {
                _newImageLiveData.postValue(it)
            }
        }
    }

    fun sendReminder(sendReminderRequest: SendReminderRequest) {
        viewModelScope.launch {
            postSendReminderUseCase.sendReminder(sendReminderRequest).collect {
                _sendReminderLiveData.postValue(it)
            }
        }
    }
}