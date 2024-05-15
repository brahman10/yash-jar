package com.jar.app.feature_gold_delivery.shared.data.network

import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_gold_delivery.shared.domain.model.DeliverProductRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductsV2
import com.jar.app.feature_transaction.shared.domain.model.TransactionData
import com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.Endpoints
import com.jar.app.feature_gold_delivery.shared.domain.model.AddWishListResponse
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIBreakdownData
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIData
import com.jar.app.feature_gold_delivery.shared.domain.model.DeliveryLandingData
import com.jar.app.feature_gold_delivery.shared.domain.model.GetWishlistAPIResponse
import com.jar.app.feature_gold_delivery.shared.domain.model.GoldDeliveryFaq
import com.jar.app.feature_gold_delivery.shared.domain.model.GoldDeliveryPlaceOrderDataRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.OrderStatusAPIResponse
import com.jar.app.feature_one_time_payments_common.shared.DeliverProductResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails
import com.jar.app.feature_user_api.domain.model.Addresses
import com.jar.app.feature_user_api.domain.model.ValidatePinCodeResponse
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

internal class StoreItemDeliveryDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun getAllStoreItems(category: String?) =
        getResult<ApiResponseWrapper<ProductsV2?>> {
            client.get {
                url(Endpoints.FETCH_ALL_STORE_ITEMS)
                if (category != null)
                    parameter("category", category)
            }
        }

    suspend fun getStoreItemFaq(category: String) =
        getResult<ApiResponseWrapper<GoldDeliveryFaq?>> {
            client.get {
                url(Endpoints.FETCH_STORE_ITEM_FAQ)
                parameter("contentType", category)
            }
        }

    suspend fun fetchOrderStatus(orderId: String) =
        getResult<ApiResponseWrapper<OrderStatusAPIResponse?>> {
            client.post {
                url(Endpoints.FETCH_ORDER_STATE)
                parameter("orderId", orderId)
            }
        }

    suspend fun fetchTransactionListPaginated(request: TransactionListingRequest) =
        getResult<ApiResponseWrapper<List<TransactionData>>> {
            client.post {
                url(Endpoints.FETCH_GOLD_DELIVERY_ORDER_TRANSACTION_LIST)
                setBody(request)
            }
        }

    suspend fun submitFeedback(orderId: String, feedback: Int) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.SUBMIT_DELIVERY_FEEDBACK)
                parameter("orderId", orderId)
                parameter("feedback", feedback.toString())
            }
        }

    suspend fun getDeliveryLandingScreenDetails() =
        getResult<ApiResponseWrapper<DeliveryLandingData?>> {
            client.get {
                url(Endpoints.FETCH_GOLD_DELIVERY_LANDING_SCREEN_DETAILS)
            }
        }

    suspend fun fetchCart() =
        getResult<ApiResponseWrapper<CartAPIData?>> {
            client.get {
                url(Endpoints.FETCH_FULL_CART)
            }
        }

    suspend fun fetchCartBreakdown() =
        getResult<ApiResponseWrapper<CartAPIBreakdownData?>> {
            client.get {
                url(Endpoints.FETCH_CART_BREAKDOWN)
            }
        }

    suspend fun postGoldDeliveryOrder(goldDeliveryPlaceOrderDataRequest: GoldDeliveryPlaceOrderDataRequest) =
        getResult<ApiResponseWrapper<InitiatePaymentResponse?>> {
            client.post {
                url(Endpoints.PLACE_GOLD_DELIVERY_ORDER)
                setBody(goldDeliveryPlaceOrderDataRequest)
            }
        }

    suspend fun addItemToCart(addCartItemRequest: AddCartItemRequest) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.ADD_ITEM_TO_CART)
                setBody(addCartItemRequest)
            }
        }

    suspend fun notifyUser(addCartItemRequest: AddCartItemRequest) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.NOTIFY_USER)
                setBody(addCartItemRequest)
            }
        }

    suspend fun addProductToWishlist(addCartItemRequest: AddCartItemRequest) =
        getResult<ApiResponseWrapper<AddWishListResponse?>> {
            client.post {
                url(Endpoints.ADD_PRODUCT_TO_WISHLIST)
                setBody(addCartItemRequest)
            }
        }

    suspend fun removeProductFromWishlist(id: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.delete {
                url(Endpoints.REMOVE_PRODUCT_FROM_WISHLIST)
                parameter("id", id)
            }
        }

    suspend fun getProductsFromWishlist(page: Int, size: Int) =
        getResult<ApiResponseWrapper<GetWishlistAPIResponse?>> {
            client.get {
                url(Endpoints.FETCH_PRODUCT_FROM_WISHLIST)
                parameter("page", page)
                parameter("size", size)
            }
        }

    suspend fun deleteItemFromCart(productID: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.delete {
                url(Endpoints.DELETE_ITEM_OF_CART)
                parameter("id", productID)
            }
        }

    suspend fun changeQuantityInCart(id: String, quantity: Int) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.put {
                url(Endpoints.UPDATE_ITEM_QUANTITY_IN_CART)
                parameter("id", id)
                parameter("quantity", quantity)
            }
        }

    suspend fun deliverProduct(deliverProductRequest: DeliverProductRequest) =
        getResult<ApiResponseWrapper<DeliverProductResponse?>> {
            client.post {
                url(Endpoints.DELIVER_PRODUCT)
                setBody(deliverProductRequest)
            }
        }

    suspend fun validatePinCode(pinCode: String) =
        getResult<ApiResponseWrapper<ValidatePinCodeResponse?>> {
            client.get {
                url(Endpoints.VALIDATE_PIN_CODE)
                parameter("pincode", pinCode)
            }
        }

    suspend fun getAddressById(id: String) =
        getResult<ApiResponseWrapper<Address?>> {
            client.get {
                url(Endpoints.FETCH_ADDRESS_BY_ID)
                parameter("id", id)
            }
        }

    suspend fun addDeliveryAddress(address: Address) =
        getResult<ApiResponseWrapper<Address?>> {
            client.post {
                url(Endpoints.ADD_ADDRESS)
                setBody(address)
            }
        }

    suspend fun deleteAddress(id: String) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.delete {
                url(Endpoints.DELETE_ADDRESS)
                parameter("id", id)
            }
        }

    suspend fun editAddress(id: String, address: Address) =
        getResult<ApiResponseWrapper<Address?>> {
            client.put {
                url(Endpoints.EDIT_ADDRESS)
                parameter("id", id)
                setBody(address)
            }
        }

    suspend fun getAllAddress() =
        getResult<ApiResponseWrapper<Addresses?>> {
            client.get {
                url(Endpoints.FETCH_ALL_ADDRESS)
            }
        }

    suspend fun fetchTxnDetails(
        orderId: String,
        assetSourceType: String,
        assetTxnId: String
    ) = getResult<ApiResponseWrapper<NewTransactionDetails?>> {
        client.get {
            url(Endpoints.FETCH_DELIVERY_TRANSACTION_ORDER_DETAIL)
            parameter("orderId", orderId)
            parameter("assetSourceType", assetSourceType)
            parameter("assetTxnId", assetTxnId)
        }
    }
}