package com.jar.app.feature_gold_delivery.impl.ui.saved_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteAddressUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetSavedAddressUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.SavedAddressFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SavedAddressFragmentViewModelAndroid @Inject constructor(
    private val getSavedAddressUseCase: GetSavedAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,

    ) : ViewModel() {

    private val viewModel by lazy {
        SavedAddressFragmentViewModel(
            getSavedAddressUseCase,
            deleteAddressUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}