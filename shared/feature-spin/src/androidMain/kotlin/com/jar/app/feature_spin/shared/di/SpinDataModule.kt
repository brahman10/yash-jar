package com.jar.app.feature_spin.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class SpinDataModule {

    @Provides
    @Singleton
    internal fun provideCommonSpinDataModule(@AppHttpClient client: HttpClient): CommonSpinDataModule {
        return CommonSpinDataModule(client)
    }

    @Provides
    @Singleton
    internal fun provideSpinDataSource(commonSpinDataModule: CommonSpinDataModule): SpinDataSource {
        return commonSpinDataModule.spinDataSource
    }

    @Provides
    @Singleton
    internal fun provideFetchJackpotOutComeDataUseCase(commonSpinDataModule: CommonSpinDataModule): FetchJackpotOutComeDataUseCase {
        return commonSpinDataModule.provideFetchJackpotOutComeDataUseCase
    }

    @Provides
    @Singleton
    internal fun provideSpinFlatOutcomeUseCase(commonSpinDataModule: CommonSpinDataModule): SpinFlatOutcomeUseCase {
        return commonSpinDataModule.provideSpinFlatOutcomeUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchSpinsResultDataUseCase(commonSpinDataModule: CommonSpinDataModule): FetchSpinsResultDataUseCase {
        return commonSpinDataModule.provideFetchSpinsResultDataUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchSpinIntroUseCase(commonSpinDataModule: CommonSpinDataModule): FetchSpinIntroUseCase {
        return commonSpinDataModule.provideFetchSpinIntroUseCase
    }

    @Provides
    @Singleton
    internal fun provideSpinRepositoryInternal(commonSpinDataModule: CommonSpinDataModule): SpinRepositoryInternal {
        return commonSpinDataModule.spinRepositoryInternal
    }

    @Provides
    @Singleton
    internal fun provideFetchUseWinningUseCase(commonSpinDataModule: CommonSpinDataModule): FetchUseWinningUseCase {
        return commonSpinDataModule.provideFetchUseWinningUseCase
    }

    @Provides
    @Singleton
    internal fun provideSpinRepositoryExternal(commonSpinDataModule: CommonSpinDataModule): SpinRepositoryExternal {
        return commonSpinDataModule.spinRepositoryExternal
    }

    @Provides
    @Singleton
    internal fun provideResetSpinUseCase(commonSpinDataModule: CommonSpinDataModule): ResetSpinsUseCase {
        return commonSpinDataModule.provideResetSpinUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchSpinDataUseCase(commonSpinDataModule: CommonSpinDataModule): FetchSpinDataUseCase {
        return commonSpinDataModule.provideFetchSpinDataUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchSpinsMetaDataUseCase(commonSpinDataModule: CommonSpinDataModule): FetchSpinsMetaDataUseCase {
        return commonSpinDataModule.provideFetchSpinsMetaDataUseCase
    }

}