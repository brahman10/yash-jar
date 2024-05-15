package com.jar.app.feature_weekly_magic.shared.di

import com.jar.app.feature_weekly_magic.shared.data.network.WeeklyChallengeDataSource
import com.jar.app.feature_weekly_magic.shared.data.repository.WeeklyChallengeRepositoryImpl
import com.jar.app.feature_weekly_magic.shared.domain.repository.WeeklyChallengeRepositoryInternal
import com.jar.app.feature_weekly_magic.shared.domain.usecase.FetchWeeklyChallengeInfoUseCase
import com.jar.app.feature_weekly_magic.shared.domain.usecase.FetchWeeklyChallengeInfoUseCaseImpl
import com.jar.app.feature_weekly_magic.shared.domain.usecase.MarkWeeklyChallengeInfoAsViewedUseCase
import com.jar.app.feature_weekly_magic.shared.domain.usecase.MarkWeeklyChallengeInfoAsViewedUseCaseImpl
import io.ktor.client.HttpClient

class CommonWeeklyChallengeDataModule(
    client: HttpClient
) {

    val weeklyChallengeDataSource: WeeklyChallengeDataSource by lazy {
        WeeklyChallengeDataSource(client)
    }


    val weeklyChallengeRepositoryInternal: WeeklyChallengeRepositoryInternal by lazy {
        WeeklyChallengeRepositoryImpl(weeklyChallengeDataSource)
    }

    val fetchWeeklyChallengeInfoUseCase: FetchWeeklyChallengeInfoUseCase by lazy {
        FetchWeeklyChallengeInfoUseCaseImpl(weeklyChallengeRepositoryInternal)
    }

    val markWeeklyChallengeInfoAsViewedUseCase: MarkWeeklyChallengeInfoAsViewedUseCase by lazy {
        MarkWeeklyChallengeInfoAsViewedUseCaseImpl(weeklyChallengeRepositoryInternal)
    }
}