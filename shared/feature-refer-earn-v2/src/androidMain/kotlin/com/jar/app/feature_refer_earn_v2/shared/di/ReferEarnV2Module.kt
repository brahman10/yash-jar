package com.jar.app.feature_refer_earn_v2.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_refer_earn_v2.shared.data.network.ReferEarnV2DataSource
import com.jar.app.feature_refer_earn_v2.shared.domain.repository.ReferEarnV2Repository
import com.jar.app.feature_refer_earn_v2.shared.domain.repository.ReferEarnV2RepositoryImpl
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralIntroStaticDataUseCase
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralsShareMessageUseCase
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralsUseCase
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.PostReferralAttributionDataUseCase
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.impl.FetchReferralIntroStaticDataUseCaseImpl
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.impl.FetchReferralsShareMessageUseCaseImpl
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.impl.FetchReferralsUseCaseImpl
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.impl.PostReferralAttributionDataUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ReferEarnV2Module {

    @Provides
    @Singleton
    internal fun provideReferEarnV2DataSource(@AppHttpClient client: HttpClient): ReferEarnV2DataSource {
        return ReferEarnV2DataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideReferEarnV2Repository(referEarnV2DataSource: ReferEarnV2DataSource): ReferEarnV2Repository {
        return ReferEarnV2RepositoryImpl(
            referEarnV2DataSource
        )
    }
    @Provides
    @Singleton
    internal fun provideFetchReferralIntroStaticDataUseCase(referRepository: ReferEarnV2Repository): FetchReferralIntroStaticDataUseCase {
        return FetchReferralIntroStaticDataUseCaseImpl(referRepository)
    }
    @Provides
    @Singleton
    internal fun provideFetchReferralsShareMessageUseCase(referRepository: ReferEarnV2Repository): FetchReferralsShareMessageUseCase {
        return FetchReferralsShareMessageUseCaseImpl(referRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchReferralDataUseCase(referRepository: ReferEarnV2Repository): FetchReferralsUseCase {
        return FetchReferralsUseCaseImpl(referRepository)
    }
    @Provides
    @Singleton
    internal fun providePostReferralAttributionDataUseCase(referRepository: ReferEarnV2Repository): PostReferralAttributionDataUseCase {
        return PostReferralAttributionDataUseCaseImpl(referRepository)
    }


}