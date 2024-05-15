package com.jar.app.feature_gold_delivery.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_gold_delivery.shared.data.network.StoreItemDeliveryDataSource
import com.jar.app.feature_gold_delivery.impl.data.repository.DeliveryRepositoryImpl
import com.jar.app.feature_gold_delivery.shared.data.repository.DeliveryRepository
import com.jar.app.feature_gold_delivery.shared.domain.use_case.*
import com.jar.app.feature_gold_delivery.shared.domain.use_case.impl.*
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DeliveryModule {

    @Provides
    @Singleton
    internal fun provideDeliveryDataSource(@AppHttpClient client: HttpClient): StoreItemDeliveryDataSource {
        return StoreItemDeliveryDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideDeliveryRepository(storeItemDeliveryDataSource: StoreItemDeliveryDataSource): DeliveryRepository {
        return DeliveryRepositoryImpl(storeItemDeliveryDataSource)
    }

    @Provides
    @Singleton
    internal fun provideAddDeliveryAddressUseCase(deliveryRepository: DeliveryRepository): AddDeliveryAddressUseCase {
        return AddDeliveryAddressUseCaseImpl(deliveryRepository)
    }


    @Provides
    @Singleton
    internal fun provideDeleteAddressUseCase(deliveryRepository: DeliveryRepository): DeleteAddressUseCase {
        return DeleteAddressUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    internal fun provideEditAddressUseCase(deliveryRepository: DeliveryRepository): EditAddressUseCase {
        return EditAddressUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    internal fun provideGetSavedAddressUseCase(deliveryRepository: DeliveryRepository): GetSavedAddressUseCase {
        return GetSavedAddressUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    internal fun provideGetAddressByIdUseCase(deliveryRepository: DeliveryRepository): GetAddressByIdUseCase {
        return GetAddressByIdUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    internal fun provideGetAllStoreItemsUseCase(deliveryRepository: DeliveryRepository): GetAllStoreItemsUseCase {
        return GetAllStoreItemsUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    internal fun provideDeliverProductUseCase(deliveryRepository: DeliveryRepository): DeliverProductUseCase {
        return DeliverProductUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    internal fun provideValidatePinCodeUseCase(deliveryRepository: DeliveryRepository): ValidatePinCodeUseCase {
        return ValidatePinCodeUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    internal fun provideGetDeliveryLandingDetailsUseCase(deliveryRepository: DeliveryRepository): GetDeliveryLandingDetailsUseCase {
        return GetDeliveryLandingDetailsUseCaseImpl(deliveryRepository)
    }


    @Provides
    @Singleton
    internal fun provideChangeQuantityInCart(deliveryRepository: DeliveryRepository): EditItemQuantityCartUseCase {
        return EditItemQuantityCartUseCaseUseCaseImpl(deliveryRepository)
    }



    @Provides
    @Singleton
    internal fun provideCartOrderUseCase(deliveryRepository: DeliveryRepository): CartOrderUseCase {
        return CartOrderUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideNotifyUserUseCase(deliveryRepository: DeliveryRepository): NotifyUserUseCase {
        return NotifyUserUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideAddProductToWishlistUseCase(deliveryRepository: DeliveryRepository): AddProductToWishlistUseCase {
        return AddProductToWishlistUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideRemoveProductFromWishlistUseCase(deliveryRepository: DeliveryRepository): RemoveProductFromWishlistUseCase {
        return RemoveProductFromWishlistUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideGetProductsFromWishlistUseCase(deliveryRepository: DeliveryRepository): GetProductsFromWishlistUseCase {
        return GetProductsFromWishlistUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideFetchCartUseCase(deliveryRepository: DeliveryRepository): FetchCartUseCase {
        return FetchCartUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideFetchMyOrdersUseCase(deliveryRepository: DeliveryRepository): FetchMyOrdersUseCase {
        return FetchMyOrdersUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideFetchOrderDetailUseCase(deliveryRepository: DeliveryRepository): FetchOrderDetailUseCase {
        return FetchOrderDetailUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideFetchCartBreakdownUseCase(deliveryRepository: DeliveryRepository): FetchCartBreakdownUseCase {
        return FetchCartBreakdownUseCaseImpl(deliveryRepository)
    }
    @Provides
    @Singleton
    fun providePostOrderUseCase(deliveryRepository: DeliveryRepository): PostOrderUseCase {
        return PostOrderUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteItemToCartUseCase(deliveryRepository: DeliveryRepository): DeleteItemToCartUseCase {
        return DeleteItemToCartUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideFetchOrderStatusUseCase(deliveryRepository: DeliveryRepository): FetchOrderStatusUseCase {
        return FetchOrderStatusUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideGetStoreItemFaqUseCase(deliveryRepository: DeliveryRepository): GetStoreItemFaqUseCase {
        return GetStoreItemFaqUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideFetchTransactionListingUseCase(deliveryRepository: DeliveryRepository): FetchTransactionListingUseCase {
        return FetchTransactionListingUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideAddItemToCartWithFlowUseCase(deliveryRepository: DeliveryRepository): AddItemToCartWithFlowUseCase {
        return AddItemToCartWithFlowUseCaseImpl(deliveryRepository)
    }

    @Provides
    @Singleton
    fun provideAddItemToCartWithoutFlowUseCase(deliveryRepository: DeliveryRepository): AddItemToCartWithoutFlowUseCase {
        return AddItemToCartWithoutFlowUseCaseImpl(deliveryRepository)
    }


}