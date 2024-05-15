package com.jar.app.feature_user_api.impl.ui.add_address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.domain.use_case.AddUserAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class UserAddAddressViewModel @Inject constructor(
    private val addUserAddressUseCase: AddUserAddressUseCase
): ViewModel() {

    private val _addressLiveData = MutableLiveData<RestClientResult<ApiResponseWrapper<Address>>>()
    val addressLiveData: LiveData<RestClientResult<ApiResponseWrapper<Address>>>
        get() = _addressLiveData

    fun addAddress(address: Address) {
        viewModelScope.launch {
            addUserAddressUseCase.addDeliveryAddress(address).collect {
                _addressLiveData.postValue(it)
            }
        }
    }

}