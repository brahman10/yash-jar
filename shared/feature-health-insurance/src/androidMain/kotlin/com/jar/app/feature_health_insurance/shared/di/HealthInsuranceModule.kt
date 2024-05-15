package com.jar.app.feature_health_insurance.shared.di


import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_health_insurance.shared.data.network.HealthInsuranceDataSource
import com.jar.app.feature_health_insurance.shared.data.repository.HealthInsuranceRepositoryImpl
import com.jar.app.feature_health_insurance.shared.domain.repository.HealthInsuranceRepository
import com.jar.app.feature_health_insurance.shared.domain.use_cases.CreateProposalUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchAddDetailsScreenStaticDataUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchBenefitsDetailsUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchIncompleteProposalUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchInsurancePlansUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchInsuranceTransactionDetailsUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchInsuranceTransactionsUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchLandingScreenDetailsUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchManageScreenDataUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchPaymentConfigUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchPaymentStatusUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchPlanComparisonUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.InitiateInsurancePlanUseCase
import com.jar.app.feature_health_insurance.shared.domain.use_cases.impl.CreateProposalUseCaseImpl
import com.jar.app.feature_health_insurance.shared.domain.use_cases.impl.FetchAddDetailsScreenStaticDataUseCaseImpl
import com.jar.app.feature_health_insurance.shared.domain.use_cases.impl.FetchBenefitsDetailsUseCaseImpl
import com.jar.app.feature_health_insurance.shared.domain.use_cases.impl.FetchIncompleteProposalUseCaseImpl
import com.jar.app.feature_health_insurance.shared.domain.use_cases.impl.FetchInsurancePlansUseCaseImpl
import com.jar.app.feature_health_insurance.shared.domain.use_cases.impl.FetchInsuranceTransactionDetailsUseCaseImpl
import com.jar.app.feature_health_insurance.shared.domain.use_cases.impl.FetchInsuranceTransactionsUseCaseImpl
import com.jar.app.feature_health_insurance.shared.domain.use_cases.impl.FetchLandingScreenDetailsUseCaseImpl
import com.jar.app.feature_health_insurance.shared.domain.use_cases.impl.FetchManageScreenDataUseCaseImpl
import com.jar.app.feature_health_insurance.shared.domain.use_cases.impl.FetchPaymentConfigUseCaseImpl
import com.jar.app.feature_health_insurance.shared.domain.use_cases.impl.FetchPaymentStatusUseCaseImpl
import com.jar.app.feature_health_insurance.shared.domain.use_cases.impl.FetchPlanComparisonUseCaseImpl
import com.jar.app.feature_health_insurance.shared.domain.use_cases.impl.InitiateInsurancePlanUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HealthInsuranceModule {

    @Provides
    @Singleton
    internal fun provideHealthInsuranceDataSource(@AppHttpClient client: HttpClient): HealthInsuranceDataSource {
        return HealthInsuranceDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideHealthInsuranceRepository(healthInsuranceDataSource: HealthInsuranceDataSource) : HealthInsuranceRepository {
        return HealthInsuranceRepositoryImpl(healthInsuranceDataSource)
    }

    @Provides
    @Singleton
    internal fun providesFetchBenefitsDetailsUseCase(healthInsuranceRepository: HealthInsuranceRepository) : FetchBenefitsDetailsUseCase {
        return FetchBenefitsDetailsUseCaseImpl(healthInsuranceRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchIncompleteProposalUseCase(healthInsuranceRepository: HealthInsuranceRepository) : FetchIncompleteProposalUseCase {
        return FetchIncompleteProposalUseCaseImpl(healthInsuranceRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchLandingScreenDetailsUseCase(healthInsuranceRepository: HealthInsuranceRepository) :  FetchLandingScreenDetailsUseCase{
        return FetchLandingScreenDetailsUseCaseImpl(healthInsuranceRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchInsurancePlansUseCase(healthInsuranceRepository: HealthInsuranceRepository) : FetchInsurancePlansUseCase {
        return FetchInsurancePlansUseCaseImpl(healthInsuranceRepository)
    }

    @Provides
    @Singleton
    internal fun providePaymentStatusUseCase(healthInsuranceRepository: HealthInsuranceRepository) : FetchPaymentStatusUseCase {
        return FetchPaymentStatusUseCaseImpl(healthInsuranceRepository)
    }

    @Provides
    @Singleton
    internal fun providePlanComparisonUseCase(healthInsuranceRepository: HealthInsuranceRepository) : FetchPlanComparisonUseCase {
        return FetchPlanComparisonUseCaseImpl(healthInsuranceRepository)
    }

    @Provides
    @Singleton
    internal fun provideInitiateInsurancePlanUseCase(healthInsuranceRepository: HealthInsuranceRepository) : InitiateInsurancePlanUseCase {
        return InitiateInsurancePlanUseCaseImpl(healthInsuranceRepository)
    }

    @Provides
    @Singleton
    internal fun provideCreateProposalUseCase(healthInsuranceRepository: HealthInsuranceRepository) : CreateProposalUseCase {
        return CreateProposalUseCaseImpl(healthInsuranceRepository)
    }

    @Provides
    @Singleton
    internal fun provideAddDetailsScreenStaticDataUseCase(healthInsuranceRepository: HealthInsuranceRepository) : FetchAddDetailsScreenStaticDataUseCase {
        return FetchAddDetailsScreenStaticDataUseCaseImpl(healthInsuranceRepository)
    }

    @Provides
    @Singleton
    internal fun providePaymentConfigUseCase(healthInsuranceRepository: HealthInsuranceRepository) : FetchPaymentConfigUseCase {
        return FetchPaymentConfigUseCaseImpl(healthInsuranceRepository)
    }

    @Provides
    @Singleton
    internal fun provideManageScreenUseCase(healthInsuranceRepository: HealthInsuranceRepository) : FetchManageScreenDataUseCase {
        return FetchManageScreenDataUseCaseImpl(healthInsuranceRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchTransactionsUseCase(healthInsuranceRepository: HealthInsuranceRepository): FetchInsuranceTransactionsUseCase {
        return FetchInsuranceTransactionsUseCaseImpl(healthInsuranceRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchTransactionDetailsUseCase(healthInsuranceRepository: HealthInsuranceRepository): FetchInsuranceTransactionDetailsUseCase {
        return FetchInsuranceTransactionDetailsUseCaseImpl(healthInsuranceRepository)
    }

}