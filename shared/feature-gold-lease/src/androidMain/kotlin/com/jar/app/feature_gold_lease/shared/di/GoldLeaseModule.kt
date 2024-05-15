package com.jar.app.feature_gold_lease.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_gold_lease.shared.data.network.GoldLeaseDataSource
import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.use_case.*
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.*
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class GoldLeaseModule {

    @Provides
    @Singleton
    internal fun provideCommonGoldLeaseModule(@AppHttpClient client: HttpClient): CommonGoldLeaseModule {
        return CommonGoldLeaseModule(client)
    }

    @Provides
    @Singleton
    internal fun provideGoldLeaseDataSource(commonGoldLeaseModule: CommonGoldLeaseModule): GoldLeaseDataSource {
        return commonGoldLeaseModule.goldLeaseDataSource
    }

    @Provides
    @Singleton
    internal fun provideGoldLeaseRepository(commonGoldLeaseModule: CommonGoldLeaseModule): GoldLeaseRepository {
        return commonGoldLeaseModule.goldLeaseRepository
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldLeaseFaqsUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeaseFaqsUseCase {
        return commonGoldLeaseModule.fetchGoldLeaseFaqsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldLeaseTermsAndConditionsUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeaseTermsAndConditionsUseCase {
        return commonGoldLeaseModule.fetchGoldLeaseTermsAndConditionsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldLeaseRiskFactorUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeaseRiskFactorUseCase {
        return commonGoldLeaseModule.fetchGoldLeaseRiskFactorUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldLeaseStatusUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeaseStatusUseCase {
        return commonGoldLeaseModule.fetchGoldLeaseStatusUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldLeasePlanFiltersUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeasePlanFiltersUseCase {
        return commonGoldLeaseModule.fetchGoldLeasePlanFiltersUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldLeasePlansUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeasePlansUseCase {
        return commonGoldLeaseModule.fetchGoldLeasePlansUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldLeaseJewellerDetailsUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeaseJewellerDetailsUseCase {
        return commonGoldLeaseModule.fetchGoldLeaseJewellerDetailsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldLeaseLandingDetailsUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeaseLandingDetailsUseCase {
        return commonGoldLeaseModule.fetchGoldLeaseLandingDetailsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldLeaseJewellerListingsUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeaseJewellerListingsUseCase {
        return commonGoldLeaseModule.fetchGoldLeaseJewellerListingsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldLeaseGoldOptionsUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeaseGoldOptionsUseCase {
        return commonGoldLeaseModule.fetchGoldLeaseGoldOptionsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldLeaseOrderSummaryUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeaseOrderSummaryUseCase {
        return commonGoldLeaseModule.fetchGoldLeaseOrderSummaryUseCase
    }

    @Provides
    @Singleton
    internal fun provideInitiateGoldLeaseV2UseCase(commonGoldLeaseModule: CommonGoldLeaseModule): InitiateGoldLeaseV2UseCase {
        return commonGoldLeaseModule.initiateGoldLeaseV2UseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchUserLeasesUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchUserLeasesUseCase {
        return commonGoldLeaseModule.fetchUserLeasesUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldLeaseMyOrdersUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeaseMyOrdersUseCase {
        return commonGoldLeaseModule.fetchGoldLeaseMyOrdersUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchUserLeaseDetailsUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchUserLeaseDetailsUseCase {
        return commonGoldLeaseModule.fetchUserLeaseDetailsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldLeaseV2TransactionsUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeaseV2TransactionsUseCase {
        return commonGoldLeaseModule.fetchGoldLeaseV2TransactionsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFFetchGoldLeaseRetryDataUseCase(commonGoldLeaseModule: CommonGoldLeaseModule): FetchGoldLeaseRetryDataUseCase {
        return commonGoldLeaseModule.fFetchGoldLeaseRetryDataUseCase
    }

    @Provides
    @Singleton
    internal fun provideGoldLeaseUtil(commonGoldLeaseModule: CommonGoldLeaseModule): GoldLeaseUtil {
        return commonGoldLeaseModule.goldLeaseUtil
    }

}