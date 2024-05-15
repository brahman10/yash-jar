package com.jar.app.feature_gold_delivery.shared.data.repository

import com.jar.app.feature_gold_delivery.shared.domain.model.GoldDeliveryFaq
import com.jar.app.feature_gold_delivery.shared.domain.model.OrderStatusAPIResponse
import com.jar.app.feature_gold_delivery.shared.domain.model.GetWishlistAPIResponse
import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.AddWishListResponse
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.domain.model.Addresses
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIData
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIBreakdownData
import com.jar.app.feature_gold_delivery.shared.domain.model.DeliverProductRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.DeliveryLandingData
import com.jar.app.feature_gold_delivery.shared.domain.model.GoldDeliveryPlaceOrderDataRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductsV2
import com.jar.app.feature_transaction.shared.domain.model.TransactionData
import com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest
import com.jar.app.feature_user_api.domain.model.ValidatePinCodeResponse
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_one_time_payments_common.shared.DeliverProductResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import kotlinx.coroutines.flow.Flow

interface DeliveryRepository : BaseRepository {

    suspend fun getAllStoreItems(category: String?): Flow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>
    suspend fun getStoreItemFaq(category: String): Flow<RestClientResult<ApiResponseWrapper<GoldDeliveryFaq?>>>
    suspend fun fetchOrderStatus(orderId: String): Flow<RestClientResult<ApiResponseWrapper<OrderStatusAPIResponse?>>>

    suspend fun deliverProduct(deliverProductRequest: DeliverProductRequest): Flow<RestClientResult<ApiResponseWrapper<DeliverProductResponse?>>>

    suspend fun getTransactionListingPaginated(request: TransactionListingRequest): RestClientResult<ApiResponseWrapper<List<TransactionData>>>

    suspend fun validatePinCode(pinCode: String): Flow<RestClientResult<ApiResponseWrapper<ValidatePinCodeResponse?>>>

    suspend fun addDeliveryAddress(address: Address): Flow<RestClientResult<ApiResponseWrapper<Address?>>>

    suspend fun addItemToCart(cartItem: AddCartItemRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun notifyUser(cartItem: AddCartItemRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>
    suspend fun addProductToWishlist(cartItem: AddCartItemRequest): Flow<RestClientResult<ApiResponseWrapper<AddWishListResponse?>>>

    suspend fun removeProductFromWishlist(id: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun getProductsFromWishlist(
        page: Int, size: Int
    ): RestClientResult<ApiResponseWrapper<GetWishlistAPIResponse?>>

    suspend fun deleteAddress(id: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun editAddress(
        id: String, address: Address
    ): Flow<RestClientResult<ApiResponseWrapper<Address?>>>

    suspend fun getAllAddress(): Flow<RestClientResult<ApiResponseWrapper<Addresses?>>>

    suspend fun getAddressById(id: String): Flow<RestClientResult<ApiResponseWrapper<Address?>>>
    suspend fun getDeliveryLandingScreenDetails(): Flow<RestClientResult<ApiResponseWrapper<DeliveryLandingData?>>>
    suspend fun fetchCart(): Flow<RestClientResult<ApiResponseWrapper<CartAPIData?>>>
    suspend fun fetchCartBreakdown(): Flow<RestClientResult<ApiResponseWrapper<CartAPIBreakdownData?>>>
    suspend fun deleteItemFromCart(productID: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun changeQuantityInCart(
        id: String, quantity: Int
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun postOrder(request: GoldDeliveryPlaceOrderDataRequest): Flow<RestClientResult<ApiResponseWrapper<InitiatePaymentResponse?>>>
    suspend fun submitFeedback(
        orderId: String, feedback: Int
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun addItemToCartWithoutFlow(cartItem: AddCartItemRequest): RestClientResult<ApiResponseWrapper<Unit?>>
    suspend fun fetchTxnDetails(
        orderId: String, assetSourceType: String, assetTxnId: String
    ): Flow<RestClientResult<ApiResponseWrapper<NewTransactionDetails?>>>
}