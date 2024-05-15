package com.jar.app.feature_user_api.impl.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.domain.mapper.toGoldBalance
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.domain.model.ValidatePinCodeResponse
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase
import com.jar.app.feature_user_api.domain.use_case.ValidateAddressPinCodeUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class BaseUserAddressViewModel @Inject constructor(
    private val validatePinCodeUseCase: ValidateAddressPinCodeUseCase,
    private val fetchUserCurrentGoldBalanceUseCase: FetchUserGoldBalanceUseCase
) : ViewModel() {
    private val _validatePinCodeLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<ValidatePinCodeResponse>>>()
    val validatePinCodeLiveData: LiveData<RestClientResult<ApiResponseWrapper<ValidatePinCodeResponse>>>
        get() = _validatePinCodeLiveData

    var userGoldBalance: Float? = null

    private val selectedAddressLiveData = SingleLiveEvent<Address>()

    val refreshAddressAction = SingleLiveEvent<Unit?>()

    fun selectAddress(address: Address) {
        selectedAddressLiveData.postValue(address)
    }

    fun fetchUserGoldBalance() {
        viewModelScope.launch {
            fetchUserCurrentGoldBalanceUseCase.fetchUserGoldBalance()
                .mapToDTO {
                    it?.let { it.toGoldBalance() }
                }
                .collect(
                onSuccess = {
                    userGoldBalance = it?.volume.orZero()
                }
            )
        }
    }

    fun validatePinCode(pinCode: String) {
        viewModelScope.launch {
            validatePinCodeUseCase.validatePinCode(pinCode).collect {
                _validatePinCodeLiveData.postValue(it)
            }
        }
    }
}