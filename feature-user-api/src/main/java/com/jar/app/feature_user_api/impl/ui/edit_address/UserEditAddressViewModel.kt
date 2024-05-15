package com.jar.app.feature_user_api.impl.ui.edit_address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.domain.use_case.EditUserAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class UserEditAddressViewModel @Inject constructor(
    private val editUserAddressUseCase: EditUserAddressUseCase
): ViewModel() {

    private val _editAddressLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Address>>>()
    val editAddressLiveData: LiveData<RestClientResult<ApiResponseWrapper<Address>>>
        get() = _editAddressLiveData

    fun editAddress(id: String, address: Address) {
        viewModelScope.launch {
            editUserAddressUseCase.editAddress(id, address).collect {
                _editAddressLiveData.postValue(it)
            }
        }
    }

}