package com.jar.app.feature_gold_delivery.impl.ui.store_item.list
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetDeliveryLandingDetailsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.ValidatePinCodeUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.DeliveryStoreItemListFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class DeliveryStoreItemListFragmentViewModelAndroid @Inject constructor(
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
    private val validatePinCodeUseCase: ValidatePinCodeUseCase,
    private val fetchCartUseCase: FetchCartUseCase,
    private val getDeliveryLandingDetailsUseCase: GetDeliveryLandingDetailsUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        DeliveryStoreItemListFragmentViewModel(
            getAllStoreItemsUseCase,
            validatePinCodeUseCase,
            fetchCartUseCase,
            getDeliveryLandingDetailsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}