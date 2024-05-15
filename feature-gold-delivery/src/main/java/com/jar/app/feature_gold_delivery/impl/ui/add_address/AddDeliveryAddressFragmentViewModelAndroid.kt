package com.jar.app.feature_gold_delivery.impl.ui.add_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddDeliveryAddressUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.AddDeliveryAddressFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddDeliveryAddressFragmentViewModelAndroid @Inject constructor(
    private val addDeliveryAddressUseCase: AddDeliveryAddressUseCase,
) : ViewModel() {


    private val viewModel by lazy {
        AddDeliveryAddressFragmentViewModel(
            addDeliveryAddressUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}