package com.jar.app.feature_lending.impl.ui.personal_details.address.select_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.domain.use_case.UpdateAddressDetailsUseCase
import com.jar.app.feature_lending.shared.ui.personal_details.address.select_address.LendingSelectAddressViewModel
import com.jar.app.feature_user_api.domain.use_case.GetUserSavedAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LendingSelectAddressViewModelAndroid @Inject constructor(
    private val getUserSavedAddressUseCase: GetUserSavedAddressUseCase,
    private val updateAddressDetailsUseCase: UpdateAddressDetailsUseCase
) : ViewModel() {

    private val viewModel by lazy {
        LendingSelectAddressViewModel(
            getUserSavedAddressUseCase = getUserSavedAddressUseCase,
            updateAddressDetailsUseCase = updateAddressDetailsUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}