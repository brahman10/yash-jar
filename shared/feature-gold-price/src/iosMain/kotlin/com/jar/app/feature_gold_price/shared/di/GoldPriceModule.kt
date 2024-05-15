package com.jar.app.feature_gold_price.shared.di

import com.jar.app.feature_gold_price.shared.data.network.GoldPriceDataSource
import com.jar.app.feature_gold_price.shared.data.repository.GoldPriceRepository
import com.jar.app.feature_gold_price.shared.domain.repository.GoldPriceRepositoryImpl
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.app.feature_gold_price.shared.domain.use_case.impl.FetchCurrentGoldBuyUseCaseImpl
import io.ktor.client.HttpClient

class GoldPriceModule(
    client: HttpClient
) {

    private val goldPriceDataSource: GoldPriceDataSource by lazy {
        GoldPriceDataSource(client)
    }

    private val goldPriceRepository: GoldPriceRepository by lazy {
        GoldPriceRepositoryImpl(goldPriceDataSource)
    }

    val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase by lazy {
        FetchCurrentGoldBuyUseCaseImpl(goldPriceRepository)
    }
}