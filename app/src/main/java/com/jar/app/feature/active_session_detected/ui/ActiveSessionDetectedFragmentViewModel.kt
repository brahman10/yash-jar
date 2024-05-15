package com.jar.app.feature.active_session_detected.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.domain.model.UserResponseData
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_onboarding.shared.domain.usecase.LogoutUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ActiveSessionDetectedFragmentViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val deviceUtils: DeviceUtils,
    private val prefs: PrefsApi,
    private val serializer: Serializer,
) : ViewModel() {

    private val _logoutLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<String?>>>()
    val logoutLiveData: LiveData<RestClientResult<ApiResponseWrapper<String?>>>
        get() = _logoutLiveData

    private val _saveUserLiveData = MutableLiveData<RestClientResult<Unit>>()
    val saveUserLiveData: LiveData<RestClientResult<Unit>>
        get() = _saveUserLiveData

    fun logoutFromOtherDevices() {
        viewModelScope.launch {
            logoutUseCase.logout(deviceUtils.getAdvertisingId(), refreshToken = null).collect {
                _logoutLiveData.postValue(it)
            }
        }
    }

    fun saveUserData(userResponseData: UserResponseData) {
        viewModelScope.launch {
            _saveUserLiveData.postValue(RestClientResult.loading())
            prefs.setRefreshToken(userResponseData.refreshToken)
            prefs.setAccessToken(userResponseData.accessToken)
            prefs.setUserStringSync(serializer.encodeToString(userResponseData.user))
            if (userResponseData.user.onboarded.orFalse())
                prefs.setOnboardingComplete()
            _saveUserLiveData.postValue(RestClientResult.success(Unit))
        }
    }
}