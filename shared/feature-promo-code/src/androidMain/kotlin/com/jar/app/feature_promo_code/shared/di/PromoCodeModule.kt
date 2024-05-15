package com.jar.app.feature_promo_code.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_promo_code.shared.data.network.PromoCodeDataSource
import com.jar.app.feature_promo_code.shared.data.repository.PromoCodeRepositoryImpl
import com.jar.app.feature_promo_code.shared.domain.repository.PromoCodeRepository
import com.jar.app.feature_promo_code.shared.domain.use_cases.ApplyPromoCodeUseCase
import com.jar.app.feature_promo_code.shared.domain.use_cases.FetchPromoCodeTransactionStatusUseCase
import com.jar.app.feature_promo_code.shared.domain.use_cases.impl.ApplyPromoCodeUseCaseImpl
import com.jar.app.feature_promo_code.shared.domain.use_cases.impl.FetchPromoCodeTransactionStatusUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PromoCodeModule {
    @Provides
    @Singleton
    internal fun providePromoCodeDataSource(@AppHttpClient client: HttpClient): PromoCodeDataSource {
        return PromoCodeDataSource(client)
    }

    @Provides
    @Singleton
    internal fun providePromoCodeRepository(promoCodeDataSource: PromoCodeDataSource) : PromoCodeRepository {
        return PromoCodeRepositoryImpl(promoCodeDataSource)
    }


    @Provides
    @Singleton
    internal fun provideApplyPromoCodeUseCase(promoCodeRepository: PromoCodeRepository) : ApplyPromoCodeUseCase {
        return ApplyPromoCodeUseCaseImpl(promoCodeRepository)
    }
    @Provides
    @Singleton
    internal fun provideFetchPromoCodeTransactionStatusUseCase(promoCodeRepository: PromoCodeRepository) : FetchPromoCodeTransactionStatusUseCase {
        return FetchPromoCodeTransactionStatusUseCaseImpl(promoCodeRepository)
    }
}