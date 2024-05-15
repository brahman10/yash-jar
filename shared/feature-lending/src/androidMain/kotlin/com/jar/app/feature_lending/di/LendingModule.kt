package com.jar.app.feature_lending.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_lending.shared.api.impl.use_case.FetchLendingV2PreApprovedDataUseCaseImpl
import com.jar.app.feature_lending.shared.api.impl.use_case.FetchLoanApplicationListUseCaseImpl
import com.jar.app.feature_lending.shared.api.impl.use_case.FetchLoanProgressStatusV2UseCaseImpl
import com.jar.app.feature_lending.shared.api.usecase.FetchLendingV2PreApprovedDataUseCase
import com.jar.app.feature_lending.shared.api.usecase.FetchLoanApplicationListUseCase
import com.jar.app.feature_lending.shared.api.usecase.FetchLoanProgressStatusV2UseCase
import com.jar.app.feature_lending.shared.data.network.LendingDataSource
import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.repository.LendingRepositoryImpl
import com.jar.app.feature_lending.shared.domain.use_case.*
import com.jar.app.feature_lending.shared.domain.use_case.impl.*
import com.jar.app.feature_lending.shared.ui.step_view.LendingStepsProgressGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class LendingModule {

    @Provides
    @Singleton
    internal fun provideLendingDataSource(@AppHttpClient client: HttpClient): LendingDataSource {
        return LendingDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideLendingRepository(lendingDataSource: LendingDataSource): LendingRepository {
        return LendingRepositoryImpl(lendingDataSource)
    }

    @Provides
    @Singleton
    internal fun provideFetchLoanApplicationsUseCase(lendingRepository: LendingRepository): FetchLoanApplicationsUseCase {
        return FetchLoanApplicationsUseCaseImpl(lendingRepository)
    }


    @Provides
    @Singleton
    internal fun provideFetchLendingFaqUseCase(lendingRepository: LendingRepository): FetchLendingFaqUseCase {
        return FetchLendingFaqUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideValidateIfscCodeUseCase(lendingRepository: LendingRepository): ValidateIfscCodeUseCase {
        return ValidateIfscCodeUseCaseImpl(lendingRepository)
    }


    @Provides
    @Singleton
    internal fun provideUpdateAddressDetailsUseCase(lendingRepository: LendingRepository): UpdateAddressDetailsUseCase {
        return UpdateAddressDetailsUseCaseImpl(lendingRepository)
    }


    @Provides
    @Singleton
    internal fun provideUpdateDrawdownUseCase(lendingRepository: LendingRepository): UpdateDrawdownUseCase {
        return UpdateDrawdownUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchLendingAgreementUseCase(lendingRepository: LendingRepository): FetchLendingAgreementUseCase {
        return FetchLendingAgreementUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideRequestLendingOtpUseCase(lendingRepository: LendingRepository): RequestLendingOtpUseCase {
        return RequestLendingOtpUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideVerifyLendingOtpUseCase(lendingRepository: LendingRepository): VerifyLendingOtpUseCase {
        return VerifyLendingOtpUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchEmiPlansUseCase(lendingRepository: LendingRepository): FetchEmiPlansUseCase {
        return FetchEmiPlansUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideStaticContentUseCase(lendingRepository: LendingRepository): FetchStaticContentUseCase {
        return FetchStaticContentUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchLoanDetailsV2UseCase(lendingRepository: LendingRepository): FetchLoanDetailsV2UseCase {
        return FetchLoanDetailsV2UseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideUpdateLoanDetailsV2UseCase(lendingRepository: LendingRepository): UpdateLoanDetailsV2UseCase {
        return UpdateLoanDetailsV2CaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideInitiateForeclosurePaymentUseCase(lendingRepository: LendingRepository): InitiateForeclosurePaymentUseCase {
        return InitiateForeclosurePaymentUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchRepaymentDetailsUseCase(lendingRepository: LendingRepository): FetchRepaymentDetailsUseCase {
        return FetchRepaymentDetailsUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideEmiTxnHistoryUseCase(lendingRepository: LendingRepository): FetchEmiTxnHistoryUseCase {
        return FetchEmiTxnHistoryUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideTxnDetailUseCase(lendingRepository: LendingRepository): FetchTransactionDetailsUseCase {
        return FetchTransactionDetailsUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchReadyCashJourneyUseCase(
        lendingRepository: LendingRepository
    ): FetchReadyCashJourneyUseCase {
        return FetchReadyCashJourneyUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchLandingContentUseCase(
        lendingRepository: LendingRepository
    ): FetchReadyCashLandingScreenContentUseCase {
        return FetchReadyCashLandingScreenContentUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideUpdateNotifyUserUseCase(lendingRepository: LendingRepository): UpdateNotifyUserUseCase {
        return UpdateNotifyUserUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideAcknowledgeOneTimeCardUseCase(lendingRepository: LendingRepository): AcknowledgeOneTimeCardUseCase {
        return AcknowledgeOneTimeCardUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideUploadBankStatementUseCase(lendingRepository: LendingRepository): UploadBankStatementUseCase {
        return UploadBankStatementUseCaseImpl(lendingRepository = lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchCamsBanksUseCase(lendingRepository: LendingRepository): FetchCamsBanksUseCase {
        return FetchCamsBanksUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchCamsDataStatusUseCase(lendingRepository: LendingRepository): FetchCamsDataStatusUseCase {
        return FetchCamsDataStatusUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchCamsSdkRedirectDataUseCase(lendingRepository: LendingRepository): FetchCamsSdkRedirectDataUseCase {
        return FetchCamsSdkRedirectDataUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchPANStatusUseCase(lendingRepository: LendingRepository): FetchPANStatusUseCase {
        return FetchPANStatusUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideUpdateCamsDowntimeUseCase(lendingRepository: LendingRepository): ScheduleBankUptimeNotificationUseCase {
        return ScheduleBankUptimeNotificationUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchRealTimeCreditDetailsUseCase(lendingRepository: LendingRepository): FetchRealTimeCreditDetailsUseCase {
        return FetchRealTimeCreditDetailsUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchExperianUseCase(lendingRepository: LendingRepository): FetchExperianReportUseCase {
        return FetchExperianReportUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideUpdateBankDetailUseCase(lendingRepository: LendingRepository): UpdateBankDetailUseCase {
        return UpdateBankDetailUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchUploadedBankStatementsUseCase(lendingRepository: LendingRepository): FetchUploadedBankStatementsUseCase {
        return FetchUploadedBankStatementsUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchRealTimeLeadStatusUseCase(lendingRepository: LendingRepository): FetchRealTimeLeadStatusUseCase {
        return FetchRealTimeLeadStatusUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideUpdateBankStatementPasswordUseCase(lendingRepository: LendingRepository): UpdateBankStatementPasswordUseCase {
        return UpdateBankStatementPasswordUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchPreApprovedUseCase(lendingRepository: LendingRepository): FetchLendingV2PreApprovedDataUseCase {
        return FetchLendingV2PreApprovedDataUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchLoanListUseCase(lendingRepository: LendingRepository): FetchLoanApplicationListUseCase {
        return FetchLoanApplicationListUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchLoanProgressStatusV2UseCase(lendingRepository: LendingRepository): FetchLoanProgressStatusV2UseCase {
        return FetchLoanProgressStatusV2UseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideLendingStepsProgressGenerator(): LendingStepsProgressGenerator {
        return LendingStepsProgressGenerator()
    }

    @Provides
    @Singleton
    internal fun provideFetchCreditReportDataUseCase(lendingRepository: LendingRepository): FetchCreditReportSummaryDataUseCase {
        return FetchCreditReportSummaryDataUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchCreditDetailedReportUseCase(lendingRepository: LendingRepository): FetchCreditDetailedReportUseCase {
        return FetchCreditDetailedReportUseCaseImpl(lendingRepository)
    }

    @Provides
    @Singleton
    internal fun provideRefreshCreditReportSummaryDataUseCase(lendingRepository: LendingRepository): RefreshCreditReportSummaryDataUseCase {
        return RefreshCreditReportSummaryDataUseCaseImpl(lendingRepository)
    }
}