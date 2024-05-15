package com.jar.app.feature_spin.shared.di

import com.jar.app.feature_spin.shared.data.network.SpinDataSource
import com.jar.app.feature_spin.shared.data.repository.SpinRepositoryImpl
import com.jar.app.feature_spin.shared.domain.repository.SpinRepositoryExternal
import com.jar.app.feature_spin.shared.domain.repository.SpinRepositoryInternal
import com.jar.app.feature_spin.shared.domain.usecase.FetchJackpotOutComeDataUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchJackpotOutComeDataUseCaseImpl
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinDataUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinDataUseCaseImpl
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinIntroUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinIntroUseCaseImpl
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinsMetaDataUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinsMetaDataUseCaseImpl
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinsResultDataUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchSpinsResultDataUseCaseImpl
import com.jar.app.feature_spin.shared.domain.usecase.FetchUseWinningUseCase
import com.jar.app.feature_spin.shared.domain.usecase.FetchUseWinningUseCaseImpl
import com.jar.app.feature_spin.shared.domain.usecase.ResetSpinUseCaseImpl
import com.jar.app.feature_spin.shared.domain.usecase.ResetSpinsUseCase
import com.jar.app.feature_spin.shared.domain.usecase.SpinFlatOutcomeUseCase
import com.jar.app.feature_spin.shared.domain.usecase.SpinFlatOutcomeUseCaseImpl
import io.ktor.client.HttpClient

class CommonSpinDataModule(
    client: HttpClient
) {

    val spinDataSource: SpinDataSource by lazy {
        SpinDataSource(client)
    }

    val spinRepositoryInternal: SpinRepositoryInternal by lazy {
        SpinRepositoryImpl(spinDataSource)
    }

    val spinRepositoryExternal: SpinRepositoryExternal by lazy {
        SpinRepositoryImpl(spinDataSource)
    }

    val provideFetchJackpotOutComeDataUseCase: FetchJackpotOutComeDataUseCase by lazy {
        FetchJackpotOutComeDataUseCaseImpl(spinRepositoryInternal)
    }

    val provideSpinFlatOutcomeUseCase: SpinFlatOutcomeUseCase by lazy {
        SpinFlatOutcomeUseCaseImpl(spinRepositoryInternal)
    }

    val provideFetchSpinsResultDataUseCase: FetchSpinsResultDataUseCase by lazy {
        FetchSpinsResultDataUseCaseImpl(spinRepositoryInternal)
    }

    val provideFetchSpinIntroUseCase: FetchSpinIntroUseCase by lazy {
        FetchSpinIntroUseCaseImpl(spinRepositoryExternal)
    }

    val provideFetchUseWinningUseCase: FetchUseWinningUseCase by lazy {
        FetchUseWinningUseCaseImpl(spinRepositoryExternal)
    }

    val provideResetSpinUseCase: ResetSpinsUseCase by lazy {
        ResetSpinUseCaseImpl(spinRepositoryInternal)
    }

    val provideFetchSpinDataUseCase: FetchSpinDataUseCase by lazy {
        FetchSpinDataUseCaseImpl(spinRepositoryInternal)
    }

    val provideFetchSpinsMetaDataUseCase: FetchSpinsMetaDataUseCase by lazy {
        FetchSpinsMetaDataUseCaseImpl(spinRepositoryExternal)
    }
}