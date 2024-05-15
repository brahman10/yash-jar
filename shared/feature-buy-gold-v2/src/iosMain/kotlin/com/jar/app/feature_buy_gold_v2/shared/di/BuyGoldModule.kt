package com.jar.app.feature_buy_gold_v2.shared.di

import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_buy_gold_v2.shared.data.network.BuyGoldV2DataSource
import com.jar.app.feature_buy_gold_v2.shared.data.repository.BuyGoldV2Repository
import com.jar.app.feature_buy_gold_v2.shared.domain.repository.BuyGoldV2RepositoryImpl
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchAuspiciousDatesUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchAuspiciousTimeUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchBuyGoldInfoUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchContextBannerUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchSuggestedAmountUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.BuyGoldUseCaseImpl
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.FetchAuspiciousDatesUseCaseImpl
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.FetchBuyGoldInfoUseCaseImpl
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.FetchContextBannerUseCaseImpl
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.FetchIsAuspiciousTimeUseCaseImpl
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.FetchSuggestedAmountUseCaseImpl
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import io.ktor.client.HttpClient

class BuyGoldModule(
    client: HttpClient,
    fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    remoteConfigApi: RemoteConfigApi
) {

    private val buyGoldV2DataSource: BuyGoldV2DataSource by lazy {
        BuyGoldV2DataSource(client)
    }

    private val buyGoldV2Repository: BuyGoldV2Repository by lazy {
        BuyGoldV2RepositoryImpl(buyGoldV2DataSource)
    }

    val fetchAuspiciousDatesUseCase: FetchAuspiciousDatesUseCase by lazy {
        FetchAuspiciousDatesUseCaseImpl(buyGoldV2Repository)
    }


    val fetchAuspiciousTimeUseCase: FetchAuspiciousTimeUseCase by lazy {
        FetchIsAuspiciousTimeUseCaseImpl(buyGoldV2Repository)
    }


    val buyGoldUseCase: BuyGoldUseCase by lazy {
        BuyGoldUseCaseImpl(
            fetchCurrentGoldPriceUseCase,
            buyGoldV2Repository,
            remoteConfigApi
        )
    }


    val fetchSuggestedAmountUseCase: FetchSuggestedAmountUseCase by lazy {
        FetchSuggestedAmountUseCaseImpl(buyGoldV2Repository)
    }


    val fetchBuyGoldInfoUseCase: FetchBuyGoldInfoUseCase by lazy {
        FetchBuyGoldInfoUseCaseImpl(buyGoldV2Repository)
    }

    val fetchContextBannerUseCase: FetchContextBannerUseCase by lazy {
        FetchContextBannerUseCaseImpl(buyGoldV2Repository)
    }

}