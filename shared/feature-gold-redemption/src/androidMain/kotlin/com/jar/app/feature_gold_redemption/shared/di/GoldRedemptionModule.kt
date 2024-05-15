package com.jar.app.feature_gold_redemption.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_gold_redemption.shared.data.network.GoldRedemptionDataSource
import com.jar.app.feature_gold_redemption.shared.data.repository.GoldRedemptionRepository
import com.jar.app.feature_gold_redemption.shared.domain.repository.GoldRedemptionRepositoryImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAbandonScreenUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllCityListUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllMyVouchersUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllStatesListUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllStoreFromCityUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherAllVouchersUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherBrandCatalogoueStaticUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherFaqsUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherGoldRedemptionIntroPart2UseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherGoldRedemptionIntroUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherInitiateOrderUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherInitiatePaymentUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherPendingOrdersUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherPurchaseHistoryUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherTxnDetailsUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherUserVouchersCountUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherViewDetailsUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherVoucherDetailUseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherAbandonScreenUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherAllCityListUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherAllMyVouchersUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherAllStatesListUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherAllStoreFromCityUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherAllVouchersUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherBrandCatalogoueStaticUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherFaqsUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherGoldRedemptionIntroPart2UseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherGoldRedemptionIntroUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherInitiateOrderUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherPaymentUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherPendingOrdersUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherPurchaseHistoryUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherTxnDetailsUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherUserVouchersCountUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherViewDetailsUseCaseImpl
import com.jar.app.feature_gold_redemption.shared.domain.use_case.impl.VoucherVoucherDetailUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GoldRedemptionModule {

    @Provides
    @Singleton
    internal fun provideGoldRedemptionDataSource(@AppHttpClient client: HttpClient): GoldRedemptionDataSource {
        return GoldRedemptionDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideGoldRedemptionRepository(goldRedemptionDataSource: GoldRedemptionDataSource): GoldRedemptionRepository {
        return GoldRedemptionRepositoryImpl(
            goldRedemptionDataSource
        )
    }


    @Provides
    @Singleton
    internal fun provideVoucherGoldRedemptionIntroUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherGoldRedemptionIntroUseCase {
        return VoucherGoldRedemptionIntroUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherGoldRedemptionIntroPart2UseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherGoldRedemptionIntroPart2UseCase {
        return VoucherGoldRedemptionIntroPart2UseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherFaqsUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherFaqsUseCase {
        return VoucherFaqsUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherBrandCatalogoueStaticUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherBrandCatalogoueStaticUseCase {
        return VoucherBrandCatalogoueStaticUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherAllVouchersUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherAllVouchersUseCase {
        return VoucherAllVouchersUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherVoucherDetailUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherVoucherDetailUseCase {
        return VoucherVoucherDetailUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherInitiateOrderUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherInitiateOrderUseCase {
        return VoucherInitiateOrderUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherAllMyVouchersUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherAllMyVouchersUseCase {
        return VoucherAllMyVouchersUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherUserVouchersCountUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherUserVouchersCountUseCase {
        return VoucherUserVouchersCountUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherPaymentUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherInitiatePaymentUseCase {
        return VoucherPaymentUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherViewDetailsUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherViewDetailsUseCase {
        return VoucherViewDetailsUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherAbandonScreenUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherAbandonScreenUseCase {
        return VoucherAbandonScreenUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherAllStatesListUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherAllStatesListUseCase {
        return VoucherAllStatesListUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherAllCityListUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherAllCityListUseCase {
        return VoucherAllCityListUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherAllStoreFromCityUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherAllStoreFromCityUseCase {
        return VoucherAllStoreFromCityUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherTxnDetailsUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherTxnDetailsUseCase {
        return VoucherTxnDetailsUseCaseImpl(goldRedemptionRepository)
    }

    @Provides
    @Singleton
    internal fun provideVoucherPurchaseHistoryUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherPurchaseHistoryUseCase {
        return VoucherPurchaseHistoryUseCaseImpl(goldRedemptionRepository)
    }


    @Provides
    @Singleton
    internal fun provideVoucherPendingOrdersUseCase(goldRedemptionRepository: GoldRedemptionRepository): VoucherPendingOrdersUseCase {
        return VoucherPendingOrdersUseCaseImpl(goldRedemptionRepository)
    }
}