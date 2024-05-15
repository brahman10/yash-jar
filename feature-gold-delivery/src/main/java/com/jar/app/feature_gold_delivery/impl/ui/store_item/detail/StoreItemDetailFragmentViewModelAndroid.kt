package com.jar.app.feature_gold_delivery.impl.ui.store_item.detail
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithFlowUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddProductToWishlistUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteItemToCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.EditItemQuantityCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetDeliveryLandingDetailsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetStoreItemFaqUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.NotifyUserUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.RemoveProductFromWishlistUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.ValidatePinCodeUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.DeliveryStoreItemListFragmentViewModel
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.StoreItemDetailFragmentViewModel
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class StoreItemDetailFragmentViewModelAndroid @Inject constructor(
    private val updateCartUseCase: AddItemToCartWithFlowUseCase,
    private val deleteItemFromCart: DeleteItemToCartUseCase,
    private val editItemQuantityCart: EditItemQuantityCartUseCase,
    private val productWishlistUseCase: AddProductToWishlistUseCase,
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
    private val fetchCartUseCase: FetchCartUseCase,
    private val validatePinCodeUseCase: ValidatePinCodeUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val removeProductFromWishlistUseCase: RemoveProductFromWishlistUseCase,
    private val notifyUserUseCase: NotifyUserUseCase,
    private val getStoreItemFaqUseCase: GetStoreItemFaqUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        StoreItemDetailFragmentViewModel(
            updateCartUseCase,
            deleteItemFromCart,
            editItemQuantityCart,
            productWishlistUseCase,
            getAllStoreItemsUseCase,
            fetchCartUseCase,
            validatePinCodeUseCase,
            fetchCurrentGoldPriceUseCase,
            removeProductFromWishlistUseCase,
            notifyUserUseCase,
            getStoreItemFaqUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}