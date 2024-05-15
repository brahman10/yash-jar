package com.jar.feature_gold_price_alerts.shared.di

import com.jar.feature_gold_price_alerts.shared.data.network.GoldPriceAlertsDataSource
import com.jar.feature_gold_price_alerts.shared.domain.repository.GoldPriceAlertsRepository
import com.jar.feature_gold_price_alerts.shared.domain.repository.GoldPriceAlertsRepositoryImpl
import com.jar.feature_gold_price_alerts.shared.domain.use_case.CreateGoldPriceAlertUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.DisableGoldPriceAlertUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchAverageBuyPriceUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchAverageBuyPriceUseCaseImpl
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldPriceTrendBottomSheetStaticDataUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldPriceTrendBottomSheetStaticDataUseCaseImpl
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldPriceTrendScreenStaticUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldPriceTrendScreenStaticUseCaseImpl
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldTrendHomeScreenTabUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldTrendUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.FetchGoldTrendUseCaseImpl
import com.jar.feature_gold_price_alerts.shared.domain.use_case.GetLatestGoldPriceAlertUseCase
import com.jar.feature_gold_price_alerts.shared.domain.use_case.impl.CreateGoldPriceAlertUseCaseImpl
import com.jar.feature_gold_price_alerts.shared.domain.use_case.impl.DisableGoldPriceAlertUseCaseImpl
import com.jar.feature_gold_price_alerts.shared.domain.use_case.impl.FetchGoldTrendHomeScreenTabUseCaseImpl
import com.jar.feature_gold_price_alerts.shared.domain.use_case.impl.GetLatestGoldPriceAlertUseCaseImpl
import io.ktor.client.HttpClient

class CommonGoldPriceAlertsModule(client: HttpClient) {

    val dataSource: GoldPriceAlertsDataSource by lazy {
        GoldPriceAlertsDataSource(client)
    }

    val repository: GoldPriceAlertsRepository by lazy {
        GoldPriceAlertsRepositoryImpl(dataSource)
    }


    val provideFetchGoldTrendUseCase: FetchGoldTrendUseCase by lazy {
        FetchGoldTrendUseCaseImpl(repository)
    }


    val provideFetchAverageBuyPriceUseCase: FetchAverageBuyPriceUseCase by lazy {
        FetchAverageBuyPriceUseCaseImpl(repository)
    }


    val provideFetchGoldPriceTrendScreenStaticUseCase: FetchGoldPriceTrendScreenStaticUseCase by lazy {
        FetchGoldPriceTrendScreenStaticUseCaseImpl(repository)
    }


    val provideCreateGoldPriceAlertUseCase: CreateGoldPriceAlertUseCase by lazy {
        CreateGoldPriceAlertUseCaseImpl(repository)
    }


    val provideGetLatestGoldPriceAlertUseCase: GetLatestGoldPriceAlertUseCase by lazy {
        GetLatestGoldPriceAlertUseCaseImpl(repository)
    }


    val provideDisableGoldPriceAlertUseCase: DisableGoldPriceAlertUseCase by lazy {
        DisableGoldPriceAlertUseCaseImpl(repository)
    }


    val provideFetchGoldPriceTrendBottomSheetStaticDataUseCase: FetchGoldPriceTrendBottomSheetStaticDataUseCase by lazy {
        FetchGoldPriceTrendBottomSheetStaticDataUseCaseImpl(repository)
    }

    val provideFetchGoldTrendHomeScreenTabUseCase: FetchGoldTrendHomeScreenTabUseCase by lazy {
        FetchGoldTrendHomeScreenTabUseCaseImpl(repository)
    }

}