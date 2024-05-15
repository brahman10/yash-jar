package com.jar.app.feature_gold_delivery.impl.ui.store_item.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteItemToCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.EditItemQuantityCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartBreakdownUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.PostOrderUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.ValidatePinCodeUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.StoreCartFragmentViewModel
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoreCartFragmentViewModelAndroid @Inject constructor(
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
    private val deleteItemFromCart: DeleteItemToCartUseCase,
    private val deleteAddressUseCase: DeleteItemToCartUseCase,
    private val validatePinCodeUseCase: ValidatePinCodeUseCase,
    private val editItemQuantityCart: EditItemQuantityCartUseCase,
    private val fetchCartBreakdownUseCase: FetchCartBreakdownUseCase,
    private val goldDeliveryPaymentUseCase: PostOrderUseCase,
    private val fetchCartUseCase: FetchCartUseCase,
) : ViewModel() {


    private val viewModel by lazy {
        StoreCartFragmentViewModel(
            fetchCurrentGoldPriceUseCase,
            getAllStoreItemsUseCase,
            deleteItemFromCart,
            deleteAddressUseCase,
            validatePinCodeUseCase,
            editItemQuantityCart,
            fetchCartBreakdownUseCase,
            goldDeliveryPaymentUseCase,
            fetchCartUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}