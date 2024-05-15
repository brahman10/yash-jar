package com.jar.app.feature_jar_duo.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_jar_duo.shared.data.network.DuoDataSource
import com.jar.app.feature_jar_duo.shared.data.repository.DuoRepository
import com.jar.app.feature_jar_duo.shared.data.repository.DuoRepositoryExternal
import com.jar.app.feature_jar_duo.shared.domain.use_case.DeleteGroupUseCase
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchDuoIntroStoryUseCase
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchGroupInfoUseCase
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchGroupListUseCase
import com.jar.app.feature_jar_duo.shared.domain.use_case.RenameGroupUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DuoModule {

    @Provides
    @Singleton
    internal fun provideCommonDuoModule(@AppHttpClient client: HttpClient): CommonDuoModule {
        return CommonDuoModule(client)
    }

    @Provides
    @Singleton
    internal fun provideDuoDataSource(commonDuoModule: CommonDuoModule): DuoDataSource {
        return commonDuoModule.duoDataSource
    }

    @Provides
    @Singleton
    internal fun provideDuoRepository(commonDuoModule: CommonDuoModule): DuoRepository {
        return commonDuoModule.duoRepository
    }

    @Provides
    @Singleton
    internal fun provideDuoRepositoryExternal(commonDuoModule: CommonDuoModule): DuoRepositoryExternal {
        return commonDuoModule.duoRepositoryExternal
    }

    @Provides
    @Singleton
    internal fun provideGroupInfoUseCase(commonDuoModule: CommonDuoModule): FetchGroupInfoUseCase {
        return commonDuoModule.provideGroupInfoUseCase
    }

    @Provides
    @Singleton
    internal fun provideListGroupsUseCase(commonDuoModule: CommonDuoModule): FetchGroupListUseCase {
        return commonDuoModule.provideListGroupsUseCase
    }

    @Provides
    @Singleton
    internal fun provideRenameGroupUseCase(commonDuoModule: CommonDuoModule): RenameGroupUseCase {
        return commonDuoModule.provideRenameGroupUseCase
    }

    @Provides
    @Singleton
    internal fun provideDeleteGroupUseCase(commonDuoModule: CommonDuoModule): DeleteGroupUseCase {
        return commonDuoModule.provideDeleteGroupUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchIntroStoryUseCase(commonDuoModule: CommonDuoModule): FetchDuoIntroStoryUseCase {
        return commonDuoModule.provideFetchIntroStoryUseCase
    }

}