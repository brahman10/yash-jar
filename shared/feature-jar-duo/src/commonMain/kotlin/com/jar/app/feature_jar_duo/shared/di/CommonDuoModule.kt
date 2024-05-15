package com.jar.app.feature_jar_duo.shared.di

import com.jar.app.feature_jar_duo.shared.data.network.DuoDataSource
import com.jar.app.feature_jar_duo.shared.data.repository.DuoRepository
import com.jar.app.feature_jar_duo.shared.data.repository.DuoRepositoryExternal
import com.jar.app.feature_jar_duo.shared.domain.repository.DuoRepositoryImpl
import com.jar.app.feature_jar_duo.shared.domain.use_case.DeleteGroupUseCase
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchDuoIntroStoryUseCase
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchGroupInfoUseCase
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchGroupListUseCase
import com.jar.app.feature_jar_duo.shared.domain.use_case.RenameGroupUseCase
import com.jar.app.feature_jar_duo.shared.domain.use_case.impl.DeleteGroupUseCaseImpl
import com.jar.app.feature_jar_duo.shared.domain.use_case.impl.FetchDuoIntroStoryUseCaseImpl
import com.jar.app.feature_jar_duo.shared.domain.use_case.impl.FetchFetchGroupInfoUseCaseImpl
import com.jar.app.feature_jar_duo.shared.domain.use_case.impl.FetchGroupListUseCaseImpl
import com.jar.app.feature_jar_duo.shared.domain.use_case.impl.RenameGroupUseCaseImpl
import io.ktor.client.HttpClient

class CommonDuoModule(
    client: HttpClient
) {

    val duoDataSource: DuoDataSource by lazy {
        DuoDataSource(client)
    }

    val duoRepository: DuoRepository by lazy {
        DuoRepositoryImpl(duoDataSource)
    }

    val duoRepositoryExternal: DuoRepositoryExternal by lazy {
        DuoRepositoryImpl(duoDataSource)
    }

    val provideGroupInfoUseCase: FetchGroupInfoUseCase by lazy {
        FetchFetchGroupInfoUseCaseImpl(duoRepository)
    }

    val provideListGroupsUseCase: FetchGroupListUseCase by lazy {
        FetchGroupListUseCaseImpl(duoRepositoryExternal)
    }

    val provideRenameGroupUseCase: RenameGroupUseCase by lazy {
        RenameGroupUseCaseImpl(duoRepository)
    }

    val provideDeleteGroupUseCase: DeleteGroupUseCase by lazy {
        DeleteGroupUseCaseImpl(duoRepository)
    }

    val provideFetchIntroStoryUseCase: FetchDuoIntroStoryUseCase by lazy {
        FetchDuoIntroStoryUseCaseImpl(duoRepository)
    }
}