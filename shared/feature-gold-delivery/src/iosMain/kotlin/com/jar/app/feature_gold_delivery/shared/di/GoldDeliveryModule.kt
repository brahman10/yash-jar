package com.jar.app.feature_gold_delivery.shared.di


import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_gold_delivery.shared.data.network.StoreItemDeliveryDataSource
import com.jar.app.feature_gold_delivery.impl.data.repository.DeliveryRepositoryImpl
import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddDeliveryAddressUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithFlowUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.AddItemToCartWithFlowUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddItemToCartWithoutFlowUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.AddItemToCartWithoutFlowUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.AddProductToWishlistUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.AddProductToWishlistUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.CartOrderUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteAddressUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeleteItemToCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.DeliverProductUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.EditAddressUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.EditItemQuantityCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartBreakdownUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.FetchCartBreakdownUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchCartUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.FetchCartUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchMyOrdersUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.FetchMyOrdersUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchOrderDetailUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.FetchOrderDetailUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchOrderStatusUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.FetchOrderStatusUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchTransactionListingUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.FetchTransactionListingUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAddressByIdUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetDeliveryLandingDetailsUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetProductsFromWishlistUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.GetProductsFromWishlistUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetSavedAddressUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetStoreItemFaqUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.GetStoreItemFaqUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.NotifyUserUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.NotifyUserUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.PostOrderUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.PostOrderUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.RemoveProductFromWishlistUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.RemoveProductFromWishlistUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.ValidatePinCodeUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.AddDeliveryAddressUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.CartOrderUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.DeleteAddressUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.DeleteItemToCartUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.DeliverProductUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.EditAddressUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.EditItemQuantityCartUseCaseUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.GetAddressByIdUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.GetAllStoreItemsUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.GetDeliveryLandingDetailsUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.GetSavedAddressUseCaseImpl
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.ValidatePinCodeUseCaseImpl
import io.ktor.client.HttpClient

class GoldDeliveryModule(
    client: HttpClient
) {

    private val deliveryDataSource: StoreItemDeliveryDataSource by lazy {
        StoreItemDeliveryDataSource(client)
    }

    private val deliveryRepository: DeliveryRepository by lazy {
        DeliveryRepositoryImpl(deliveryDataSource)
    }

    val provideAddDeliveryAddressUseCase: AddDeliveryAddressUseCase by lazy {
        AddDeliveryAddressUseCaseImpl(deliveryRepository)
    }


    val provideDeleteAddressUseCase: DeleteAddressUseCase by lazy {
        DeleteAddressUseCaseImpl(deliveryRepository)
    }

    val provideEditAddressUseCase: EditAddressUseCase by lazy {
        EditAddressUseCaseImpl(deliveryRepository)
    }

    val provideGetSavedAddressUseCase: GetSavedAddressUseCase by lazy {
        GetSavedAddressUseCaseImpl(deliveryRepository)
    }

    val provideGetAddressByIdUseCase: GetAddressByIdUseCase by lazy {
        GetAddressByIdUseCaseImpl(deliveryRepository)
    }

    val provideGetAllStoreItemsUseCase: GetAllStoreItemsUseCase by lazy {
        GetAllStoreItemsUseCaseImpl(deliveryRepository)
    }

    val provideDeliverProductUseCase: DeliverProductUseCase by lazy {
        DeliverProductUseCaseImpl(deliveryRepository)
    }

    val provideValidatePinCodeUseCase: ValidatePinCodeUseCase by lazy {
        ValidatePinCodeUseCaseImpl(deliveryRepository)
    }

    val provideGetDeliveryLandingDetailsUseCase: GetDeliveryLandingDetailsUseCase by lazy {
        GetDeliveryLandingDetailsUseCaseImpl(deliveryRepository)
    }


    val provideChangeQuantityInCart: EditItemQuantityCartUseCase by lazy {
        EditItemQuantityCartUseCaseUseCaseImpl(deliveryRepository)
    }


    val provideCartOrderUseCase: CartOrderUseCase by lazy {
        CartOrderUseCaseImpl(deliveryRepository)
    }

    val provideNotifyUserUseCase: NotifyUserUseCase by lazy {
        NotifyUserUseCaseImpl(deliveryRepository)
    }

    val provideAddProductToWishlistUseCase: AddProductToWishlistUseCase by lazy {
        AddProductToWishlistUseCaseImpl(deliveryRepository)
    }

    val provideRemoveProductFromWishlistUseCase: RemoveProductFromWishlistUseCase by lazy {
        RemoveProductFromWishlistUseCaseImpl(deliveryRepository)
    }

    val provideGetProductsFromWishlistUseCase: GetProductsFromWishlistUseCase by lazy {
        GetProductsFromWishlistUseCaseImpl(deliveryRepository)
    }

    val provideFetchCartUseCase: FetchCartUseCase by lazy {
        FetchCartUseCaseImpl(deliveryRepository)
    }

    val provideFetchMyOrdersUseCase: FetchMyOrdersUseCase by lazy {
        FetchMyOrdersUseCaseImpl(deliveryRepository)
    }

    val provideFetchOrderDetailUseCase: FetchOrderDetailUseCase by lazy {
        FetchOrderDetailUseCaseImpl(deliveryRepository)
    }

    val provideFetchCartBreakdownUseCase: FetchCartBreakdownUseCase by lazy {
        FetchCartBreakdownUseCaseImpl(deliveryRepository)
    }

    val providePostOrderUseCase: PostOrderUseCase by lazy {
        PostOrderUseCaseImpl(deliveryRepository)
    }

    val provideDeleteItemToCartUseCase: DeleteItemToCartUseCase by lazy {
        DeleteItemToCartUseCaseImpl(deliveryRepository)
    }

    val provideFetchOrderStatusUseCase: FetchOrderStatusUseCase by lazy {
        FetchOrderStatusUseCaseImpl(deliveryRepository)
    }

    val provideGetStoreItemFaqUseCase: GetStoreItemFaqUseCase by lazy {
        GetStoreItemFaqUseCaseImpl(deliveryRepository)
    }

    val provideFetchTransactionListingUseCase: FetchTransactionListingUseCase by lazy {
        FetchTransactionListingUseCaseImpl(deliveryRepository)
    }

    val provideAddItemToCartWithFlowUseCase: AddItemToCartWithFlowUseCase by lazy {
        AddItemToCartWithFlowUseCaseImpl(deliveryRepository)
    }

    val provideAddItemToCartWithoutFlowUseCase: AddItemToCartWithoutFlowUseCase by lazy {
        AddItemToCartWithoutFlowUseCaseImpl(deliveryRepository)
    }
}
