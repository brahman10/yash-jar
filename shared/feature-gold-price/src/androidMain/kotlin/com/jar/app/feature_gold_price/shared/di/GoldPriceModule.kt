package com.jar.app.feature_gold_price.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_gold_price.shared.data.GoldPriceFlow
import com.jar.app.feature_gold_price.shared.data.network.GoldPriceDataSource
import com.jar.app.feature_gold_price.shared.data.repository.GoldPriceRepository
import com.jar.app.feature_gold_price.shared.domain.repository.GoldPriceRepositoryImpl
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_gold_price.shared.domain.use_case.impl.FetchCurrentGoldBuyUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class GoldPriceModule {

    @Provides
    @Singleton
    internal fun provideGoldPriceDataSource(@AppHttpClient client: HttpClient): GoldPriceDataSource {
        return GoldPriceDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideGoldPriceRepository(goldPriceDataSource: GoldPriceDataSource): GoldPriceRepository {
        return GoldPriceRepositoryImpl(goldPriceDataSource)
    }


    @Provides
    @Singleton
    internal fun provideFetchCurrentGoldPriceUseCase(goldPriceRepository: GoldPriceRepository): FetchCurrentGoldPriceUseCase {
        return FetchCurrentGoldBuyUseCaseImpl(goldPriceRepository)
    }

    @Provides
    @Singleton
    internal fun provideSellPriceFlow(
        appScope: CoroutineScope,
        fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase
    ): GoldPriceFlow {
        return GoldPriceFlow(fetchCurrentGoldPriceUseCase, appScope)
    }
}