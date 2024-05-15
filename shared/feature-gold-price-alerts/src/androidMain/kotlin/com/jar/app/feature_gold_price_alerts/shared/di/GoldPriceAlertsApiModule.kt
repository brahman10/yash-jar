package com.jar.app.feature_gold_price_alerts.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.feature_gold_price_alerts.shared.di.CommonGoldPriceAlertsModule
import com.jar.feature_gold_price_alerts.shared.domain.repository.GoldPriceAlertsRepository
import com.jar.feature_gold_price_alerts.shared.domain.use_case.CreateGoldPriceAlertUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.DisableGoldPriceAlertUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchAverageBuyPriceUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldPriceTrendBottomSheetStaticDataUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldPriceTrendScreenStaticUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldTrendHomeScreenTabUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldTrendUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.GetLatestGoldPriceAlertUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class GoldPriceAlertsApiModule {

    @Provides
    @Singleton
    internal fun providesCommonGoldPriceAlertsModule(@AppHttpClient client: HttpClient): CommonGoldPriceAlertsModule {
        return CommonGoldPriceAlertsModule(client)
    }

    @Provides
    @Singleton
    internal fun provideGoldPriceAlertsRepository(commonGoldPriceAlertsModule: CommonGoldPriceAlertsModule): GoldPriceAlertsRepository {
        return commonGoldPriceAlertsModule.repository
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldTrendUseCase(commonGoldPriceAlertsModule: CommonGoldPriceAlertsModule): FetchGoldTrendUseCase {
        return commonGoldPriceAlertsModule.provideFetchGoldTrendUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchAverageBuyPriceUseCase(commonGoldPriceAlertsModule: CommonGoldPriceAlertsModule): FetchAverageBuyPriceUseCase {
        return commonGoldPriceAlertsModule.provideFetchAverageBuyPriceUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldPriceTrendScreenStaticUseCase(commonGoldPriceAlertsModule: CommonGoldPriceAlertsModule): FetchGoldPriceTrendScreenStaticUseCase {
        return commonGoldPriceAlertsModule.provideFetchGoldPriceTrendScreenStaticUseCase
    }

    @Provides
    @Singleton
    internal fun provideCreateGoldPriceAlertUseCase(commonGoldPriceAlertsModule: CommonGoldPriceAlertsModule): CreateGoldPriceAlertUseCase {
        return commonGoldPriceAlertsModule.provideCreateGoldPriceAlertUseCase
    }

    @Provides
    @Singleton
    internal fun provideGetLatestGoldPriceAlertUseCase(commonGoldPriceAlertsModule: CommonGoldPriceAlertsModule): GetLatestGoldPriceAlertUseCase {
        return commonGoldPriceAlertsModule.provideGetLatestGoldPriceAlertUseCase
    }

    @Provides
    @Singleton
    internal fun provideDisableGoldPriceAlertUseCase(commonGoldPriceAlertsModule: CommonGoldPriceAlertsModule): DisableGoldPriceAlertUseCase {
        return commonGoldPriceAlertsModule.provideDisableGoldPriceAlertUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldPriceTrendBottomSheetStaticDataUseCase(commonGoldPriceAlertsModule: CommonGoldPriceAlertsModule): FetchGoldPriceTrendBottomSheetStaticDataUseCase {
        return commonGoldPriceAlertsModule.provideFetchGoldPriceTrendBottomSheetStaticDataUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldTrendHomeScreenTabUseCase(commonGoldPriceAlertsModule: CommonGoldPriceAlertsModule): FetchGoldTrendHomeScreenTabUseCase {
        return commonGoldPriceAlertsModule.provideFetchGoldTrendHomeScreenTabUseCase
    }
}