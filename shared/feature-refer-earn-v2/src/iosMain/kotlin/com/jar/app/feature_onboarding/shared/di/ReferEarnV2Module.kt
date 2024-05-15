package com.jar.app.feature_onboarding.shared.di

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
import io.ktor.client.HttpClient

class ReferEarnV2Module(
    httpClient: HttpClient
) {

    private val referEarnV2DataSource by lazy {
        ReferEarnV2DataSource(httpClient)
    }

    private val referRepository by lazy {
        ReferEarnV2RepositoryImpl(referEarnV2DataSource)
    }

    fun fetchReferralIntroStaticDataUseCase(): FetchReferralIntroStaticDataUseCase {
        return FetchReferralIntroStaticDataUseCaseImpl(referRepository)
    }
    fun fetchReferralsShareMessageUseCase(): FetchReferralsShareMessageUseCase {
        return FetchReferralsShareMessageUseCaseImpl(referRepository)
    }

    fun fetchReferralDataUseCase(): FetchReferralsUseCase {
        return FetchReferralsUseCaseImpl(referRepository)
    }
    fun postReferralAttributionDataUseCase(): PostReferralAttributionDataUseCase {
        return PostReferralAttributionDataUseCaseImpl(referRepository)
    }

}