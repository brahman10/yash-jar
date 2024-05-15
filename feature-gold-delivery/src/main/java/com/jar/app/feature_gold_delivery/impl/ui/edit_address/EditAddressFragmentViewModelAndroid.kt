package com.jar.app.feature_gold_delivery.impl.ui.edit_address

import com.jar.app.feature_gold_delivery.shared.domain.use_case.EditAddressUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddDeliveryAddressUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.AddDeliveryAddressFragmentViewModel
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.EditAddressFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditAddressFragmentViewModelAndroid @Inject constructor(
    private val editAddressUseCase: EditAddressUseCase,

    ) : ViewModel() {


    private val viewModel by lazy {
        EditAddressFragmentViewModel(
            editAddressUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}