package com.jar.app.feature_buy_gold_v2.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_buy_gold_v2.shared.data.network.BuyGoldV2DataSource
import com.jar.app.feature_buy_gold_v2.shared.data.repository.BuyGoldV2Repository
import com.jar.app.feature_buy_gold_v2.shared.domain.repository.BuyGoldV2RepositoryImpl
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.*
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.*
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class BuyGoldV2Module {

    @Provides
    @Singleton
    internal fun provideBuyGoldV2DataSource(@AppHttpClient client: HttpClient): BuyGoldV2DataSource {
        return BuyGoldV2DataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideBuyGoldV2Repository(buyGoldV2DataSource: BuyGoldV2DataSource): BuyGoldV2Repository {
        return BuyGoldV2RepositoryImpl(buyGoldV2DataSource)
    }

    @Provides
    @Singleton
    internal fun provideFetchAuspiciousDatesUseCase(buyGoldV2Repository: BuyGoldV2Repository): FetchAuspiciousDatesUseCase {
        return FetchAuspiciousDatesUseCaseImpl(buyGoldV2Repository)
    }

    @Provides
    @Singleton
    internal fun provideFetchAuspiciousTimeUseCase(buyGoldV2Repository: BuyGoldV2Repository): FetchAuspiciousTimeUseCase {
        return FetchIsAuspiciousTimeUseCaseImpl(buyGoldV2Repository)
    }

    @Provides
    @Singleton
    internal fun provideBuyGoldUseCase(
        fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
        buyGoldV2Repository: BuyGoldV2Repository,
        remoteConfigApi: RemoteConfigApi
    ): BuyGoldUseCase {
        return BuyGoldUseCaseImpl(
            fetchCurrentGoldPriceUseCase,
            buyGoldV2Repository,
            remoteConfigApi
        )
    }

    @Provides
    @Singleton
    internal fun provideFetchSuggestedAmountUseCase(buyGoldV2Repository: BuyGoldV2Repository): FetchSuggestedAmountUseCase {
        return FetchSuggestedAmountUseCaseImpl(buyGoldV2Repository)
    }

    @Provides
    @Singleton
    internal fun provideFetchBuyGoldInfoUseCase(buyGoldV2Repository: BuyGoldV2Repository): FetchBuyGoldInfoUseCase {
        return FetchBuyGoldInfoUseCaseImpl(buyGoldV2Repository)
    }

    @Provides
    @Singleton
    internal fun provideFetchBuyGoldBottomSheetV2UseCase(buyGoldV2Repository: BuyGoldV2Repository): FetchBuyGoldBottomSheetV2UseCase {
        return FetchBuyGoldBottomSheetV2UseCaseImpl(buyGoldV2Repository)
    }

    @Provides
    @Singleton
    internal fun provideFetchFetchContextBannerUseCase(buyGoldV2Repository: BuyGoldV2Repository): FetchContextBannerUseCase {
        return FetchContextBannerUseCaseImpl(buyGoldV2Repository)
    }

    @Provides
    @Singleton
    internal fun provideFetchBuyGoldAbandonDataUseCase(buyGoldV2Repository: BuyGoldV2Repository): FetchBuyGoldAbandonDataUseCase {
        return FetchBuyGoldAbandonDataUseCaseImpl(buyGoldV2Repository)
    }
}