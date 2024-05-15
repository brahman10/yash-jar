package com.jar.app.feature_gold_delivery.impl.data.repository

import com.jar.app.feature_gold_delivery.shared.data.network.StoreItemDeliveryDataSource
import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_gold_delivery.shared.domain.model.DeliverProductRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.GoldDeliveryPlaceOrderDataRequest
import com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest
import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository

internal class DeliveryRepositoryImpl constructor(private val storeItemDeliveryDataSource: StoreItemDeliveryDataSource) :
    DeliveryRepository {

    override suspend fun getAllStoreItems(category: String?) =
        getFlowResult { storeItemDeliveryDataSource.getAllStoreItems(category) }

    override suspend fun getStoreItemFaq(category: String) =
        getFlowResult { storeItemDeliveryDataSource.getStoreItemFaq(category) }

    override suspend fun fetchOrderStatus(orderId: String) =
        getFlowResult { storeItemDeliveryDataSource.fetchOrderStatus(orderId) }

    override suspend fun getDeliveryLandingScreenDetails() =
        getFlowResult { storeItemDeliveryDataSource.getDeliveryLandingScreenDetails() }

    override suspend fun fetchCart() =
        getFlowResult {
            storeItemDeliveryDataSource.fetchCart()
        }

    override suspend fun fetchCartBreakdown() =
        getFlowResult {
            storeItemDeliveryDataSource.fetchCartBreakdown()
        }

    override suspend fun deliverProduct(deliverProductRequest: DeliverProductRequest) =
        getFlowResult {
            storeItemDeliveryDataSource.deliverProduct(deliverProductRequest)
        }

    override suspend fun getTransactionListingPaginated(request: TransactionListingRequest) =
        storeItemDeliveryDataSource.fetchTransactionListPaginated(request)


    override suspend fun validatePinCode(pinCode: String) = getFlowResult {
        storeItemDeliveryDataSource.validatePinCode(pinCode)
    }

    override suspend fun addDeliveryAddress(address: Address) =
        getFlowResult { storeItemDeliveryDataSource.addDeliveryAddress(address) }

    override suspend fun addItemToCart(cartItem: AddCartItemRequest) =
        getFlowResult {
            storeItemDeliveryDataSource.addItemToCart(cartItem)
        }

    override suspend fun addItemToCartWithoutFlow(cartItem: AddCartItemRequest) =
        storeItemDeliveryDataSource.addItemToCart(cartItem)

    override suspend fun fetchTxnDetails(
        orderId: String,
        assetSourceType: String,
        assetTxnId: String
    ) = getFlowResult {
        storeItemDeliveryDataSource.fetchTxnDetails(orderId, assetSourceType, assetTxnId)
    }

    override suspend fun notifyUser(cartItem: AddCartItemRequest) =
        getFlowResult { storeItemDeliveryDataSource.notifyUser(cartItem) }

    override suspend fun addProductToWishlist(cartItem: AddCartItemRequest) = getFlowResult {
        storeItemDeliveryDataSource.addProductToWishlist(cartItem)
    }

    override suspend fun removeProductFromWishlist(id: String) = getFlowResult {
        storeItemDeliveryDataSource.removeProductFromWishlist(id)
    }

    override suspend fun getProductsFromWishlist(page: Int, size: Int) =
        storeItemDeliveryDataSource.getProductsFromWishlist(page, size)

    override suspend fun deleteItemFromCart(productID: String) =
        getFlowResult {
            storeItemDeliveryDataSource.deleteItemFromCart(productID)
        }

    override suspend fun changeQuantityInCart(
        id: String,
        quantity: Int
    ) = getFlowResult {
        storeItemDeliveryDataSource.changeQuantityInCart(id, quantity)
    }

    override suspend fun postOrder(request: GoldDeliveryPlaceOrderDataRequest) = getFlowResult {
        storeItemDeliveryDataSource.postGoldDeliveryOrder(request)
    }

    override suspend fun submitFeedback(
        orderId: String,
        feedback: Int
    ) = getFlowResult {
        storeItemDeliveryDataSource.submitFeedback(orderId, feedback)
    }


    override suspend fun deleteAddress(id: String) =
        getFlowResult { storeItemDeliveryDataSource.deleteAddress(id) }

    override suspend fun editAddress(
        id: String,
        address: Address
    ) = getFlowResult { storeItemDeliveryDataSource.editAddress(id, address) }

    override suspend fun getAllAddress() = getFlowResult {
        storeItemDeliveryDataSource.getAllAddress()
    }

    override suspend fun getAddressById(id: String) =
        getFlowResult { storeItemDeliveryDataSource.getAddressById(id) }
}