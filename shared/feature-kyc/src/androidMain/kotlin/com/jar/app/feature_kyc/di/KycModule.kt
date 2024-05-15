package com.jar.app.feature_kyc.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_kyc.shared.api.use_case.PostFaceMatchRequestUseCase
import com.jar.app.feature_kyc.shared.api.use_case.PostKycOcrRequestUseCase
import com.jar.app.feature_kyc.shared.data.network.KycDataSource
import com.jar.app.feature_kyc.shared.data.repository.KycRepository
import com.jar.app.feature_kyc.shared.di.CommonKycModule
import com.jar.app.feature_kyc.shared.domain.use_case.*
import com.jar.app.feature_kyc.shared.domain.use_case.impl.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class KycModule {

    @Provides
    @Singleton
    internal fun provideCommonKycModule(@AppHttpClient client: HttpClient): CommonKycModule {
        return CommonKycModule(client)
    }

    @Provides
    @Singleton
    internal fun provideKycDataSource(commonKycModule: CommonKycModule): KycDataSource {
        return commonKycModule.kycDataSource
    }

    @Provides
    @Singleton
    internal fun provideKycRepository(commonKycModule: CommonKycModule): KycRepository {
        return commonKycModule.kycRepository
    }

    @Provides
    @Singleton
    internal fun provideFetchKycDetailsUseCase(commonKycModule: CommonKycModule): FetchKycDetailsUseCase {
        return commonKycModule.provideFetchKycDetailsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchKycFaqUseCase(commonKycModule: CommonKycModule): FetchKycFaqUseCase {
        return commonKycModule.provideFetchKycFaqUseCase
    }

    @Provides
    @Singleton
    internal fun providePostManualKycRequestUseCase(commonKycModule: CommonKycModule): PostManualKycRequestUseCase {
        return commonKycModule.providePostManualKycRequestUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchKycDocumentsListUseCase(commonKycModule: CommonKycModule): FetchKycDocumentsListUseCase {
        return commonKycModule.provideFetchKycDocumentsListUseCase
    }

    @Provides
    @Singleton
    internal fun providePostKycOcrRequestUseCase(commonKycModule: CommonKycModule): PostKycOcrRequestUseCase {
        return commonKycModule.providePostKycOcrRequestUseCase
    }

    @Provides
    @Singleton
    internal fun providePostFaceMatchRequestUseCase(commonKycModule: CommonKycModule): PostFaceMatchRequestUseCase {
        return commonKycModule.providePostFaceMatchRequestUseCase
    }

    @Provides
    @Singleton
    internal fun providePostPanOcrRequestUseCase(commonKycModule: CommonKycModule): PostPanOcrRequestUseCase {
        return commonKycModule.providePostPanOcrRequestUseCase
    }
}