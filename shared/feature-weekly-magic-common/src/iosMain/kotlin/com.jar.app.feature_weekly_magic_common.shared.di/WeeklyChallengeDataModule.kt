package com.jar.app.feature_weekly_magic_common.shared.di

import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_weekly_magic_common.shared.data.network.WeeklyChallengeDataSource
import com.jar.app.feature_weekly_magic_common.shared.data.repository.WeeklyChallengeRepositoryImpl
import com.jar.app.feature_weekly_magic_common.shared.domain.repository.WeeklyChallengeRepositoryExternal
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeDetailUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeDetailUseCaseImpl
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCaseImpl
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeOnBoardedUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeOnBoardedUseCaseImpl
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeViewedUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.MarkWeeklyChallengeViewedUseCaseImpl
import com.jar.internal.library.jar_core_network.api.util.Serializer
import io.ktor.client.HttpClient

class WeeklyChallengeDataModule(
    client: HttpClient,
    prefsApi: PrefsApi,
    serializer: Serializer
) {

    private val weeklyChallengeDataSource by lazy {
        WeeklyChallengeDataSource(client)
    }

    private val weeklyChallengeRepositoryExternal: WeeklyChallengeRepositoryExternal by lazy {
        WeeklyChallengeRepositoryImpl(
            weeklyChallengeDataSource,
            prefsApi,
            serializer
        )
    }

    val fetchWeeklyChallengeDetailUseCase: FetchWeeklyChallengeDetailUseCase by lazy {
        FetchWeeklyChallengeDetailUseCaseImpl(weeklyChallengeRepositoryExternal)
    }

    val fetchWeeklyChallengeMetaDataUseCase: FetchWeeklyChallengeMetaDataUseCase by lazy {
        FetchWeeklyChallengeMetaDataUseCaseImpl(weeklyChallengeRepositoryExternal)
    }

    val markWeeklyChallengeOnBoardedUseCase: MarkWeeklyChallengeOnBoardedUseCase by lazy {
        MarkWeeklyChallengeOnBoardedUseCaseImpl(weeklyChallengeRepositoryExternal)
    }

    val markWeeklyChallengeViewedUseCase: MarkWeeklyChallengeViewedUseCase by lazy {
        MarkWeeklyChallengeViewedUseCaseImpl(weeklyChallengeRepositoryExternal)
    }

}