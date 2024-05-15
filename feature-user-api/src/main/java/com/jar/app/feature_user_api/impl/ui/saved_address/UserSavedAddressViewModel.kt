package com.jar.app.feature_user_api.impl.ui.saved_address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_user_api.domain.model.Addresses
import com.jar.app.feature_user_api.domain.use_case.DeleteUserAddressUseCase
import com.jar.app.feature_user_api.domain.use_case.GetUserSavedAddressUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class UserSavedAddressViewModel @Inject constructor(
    private val getUserSavedAddressUseCase: GetUserSavedAddressUseCase,
    private val deleteUserAddressUseCase: DeleteUserAddressUseCase
) : ViewModel() {
    private val _savedAddressLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Addresses>>>()
    val savedAddressLiveData: LiveData<RestClientResult<ApiResponseWrapper<Addresses>>>
        get() = _savedAddressLiveData

    private val _deleteAddressLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val deleteAddressLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _deleteAddressLiveData

    fun getSavedAddress() {
        viewModelScope.launch {
            getUserSavedAddressUseCase.getSavedAddress().collect {
                _savedAddressLiveData.postValue(it)
            }
        }
    }

    fun deleteAddress(id: String) {
        viewModelScope.launch {
            deleteUserAddressUseCase.deleteAddress(id).collect {
                _deleteAddressLiveData.postValue(it)
            }
        }
    }
}