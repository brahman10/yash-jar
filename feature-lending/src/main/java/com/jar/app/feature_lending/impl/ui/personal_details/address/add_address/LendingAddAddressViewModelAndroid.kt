package com.jar.app.feature_lending.impl.ui.personal_details.address.add_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.shared.ui.personal_details.address.add_address.LendingAddAddressViewModel
import com.jar.app.feature_user_api.domain.use_case.AddUserAddressUseCase
import com.jar.app.feature_user_api.domain.use_case.EditUserAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LendingAddAddressViewModelAndroid @Inject constructor(
    private val addUserAddressUseCase: AddUserAddressUseCase,
    private val editUserAddressUseCase: EditUserAddressUseCase
) : ViewModel() {

    private val viewModel by lazy {
        LendingAddAddressViewModel(
            addUserAddressUseCase = addUserAddressUseCase,
            editUserAddressUseCase = editUserAddressUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel
}