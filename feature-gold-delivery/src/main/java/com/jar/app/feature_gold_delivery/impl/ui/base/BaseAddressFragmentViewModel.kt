package com.jar.app.feature_gold_delivery.impl.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.ValidatePinCodeUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.BaseAddressFragmentViewModel
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.CartItemsDeleteViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BaseAddressFragmentViewModelAndroid @Inject constructor(
    private val validatePinCodeUseCase: ValidatePinCodeUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        BaseAddressFragmentViewModel(
            validatePinCodeUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}