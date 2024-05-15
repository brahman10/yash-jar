package  com.jar.app.feature_gold_delivery.impl.ui.cart_items_quantity_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithFlowUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddProductToWishlistUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteItemToCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.RemoveProductFromWishlistUseCase
import com.jar.app.feature_gold_delivery.shared.ui.store_item.list.CartItemsQuantityEditQuantityEditFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartItemsQuantityEditQuantityEditFragmentViewModelAndroid @Inject constructor(
    private val deleteAddressUseCase: DeleteItemToCartUseCase,
    private val productWishlistUseCase: AddProductToWishlistUseCase,
    private val updateCartUseCase: AddItemToCartWithFlowUseCase,
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
    private val removeProductFromWishlistUseCase: RemoveProductFromWishlistUseCase,

    ) : ViewModel() {

    private val viewModel by lazy {
        CartItemsQuantityEditQuantityEditFragmentViewModel(
            deleteAddressUseCase,
            productWishlistUseCase,
            updateCartUseCase,
            getAllStoreItemsUseCase,
            removeProductFromWishlistUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}