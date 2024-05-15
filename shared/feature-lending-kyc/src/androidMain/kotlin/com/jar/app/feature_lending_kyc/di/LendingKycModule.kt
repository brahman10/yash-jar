package com.jar.app.feature_lending_kyc.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_lending_kyc.shared.api.impl.FetchKycProgressUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.api.use_case.FetchKycProgressUseCase
import com.jar.app.feature_lending_kyc.shared.data.network.LendingKycDataSource
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.di.CommonLendingKycModule
import com.jar.app.feature_lending_kyc.shared.domain.repository.LendingKycRepositoryImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.*
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class LendingKycModule {

    @Provides
    @Singleton
    internal fun provideCommonLendingKycModule(@AppHttpClient client: HttpClient): CommonLendingKycModule {
        return CommonLendingKycModule(client)
    }

    @Provides
    @Singleton
    internal fun provideLendingKycDataSource(commonLendingKycModule: CommonLendingKycModule): LendingKycDataSource {
        return commonLendingKycModule.lendingKycDataSource
    }

    @Provides
    @Singleton
    internal fun provideLendingKycRepository(commonLendingKycModule: CommonLendingKycModule): LendingKycRepository {
        return commonLendingKycModule.lendingKycRepository
    }

    @Provides
    @Singleton
    internal fun provideFetchJarVerifiedUserPanUseCase(commonLendingKycModule: CommonLendingKycModule): FetchJarVerifiedUserPanUseCase {
        return commonLendingKycModule.provideFetchJarVerifiedUserPanUseCase
    }

    @Provides
    @Singleton
    internal fun provideSearchCKycAadhaarDetailsUseCase(commonLendingKycModule: CommonLendingKycModule): SearchCkycAadhaarDetailsUseCase {
        return commonLendingKycModule.provideSearchCKycAadhaarDetailsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchKycAadhaarDetailsUseCase(commonLendingKycModule: CommonLendingKycModule): FetchKycAadhaarDetailsUseCase {
        return commonLendingKycModule.provideFetchKycAadhaarDetailsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchKycProgressUseCase(commonLendingKycModule: CommonLendingKycModule): FetchKycProgressUseCase {
        return commonLendingKycModule.provideFetchKycProgressUseCase
    }

    @Provides
    @Singleton
    internal fun provideRequestAadhaarOtpUseCase(commonLendingKycModule: CommonLendingKycModule): RequestAadhaarOtpUseCase {
        return commonLendingKycModule.provideRequestAadhaarOtpUseCase
    }

    @Provides
    @Singleton
    internal fun provideRequestCreditReportOtpUseCase(commonLendingKycModule: CommonLendingKycModule): RequestCreditReportOtpUseCase {
        return commonLendingKycModule.provideRequestCreditReportOtpUseCase
    }

    @Provides
    @Singleton
    internal fun provideRequestEmailOtpUseCase(commonLendingKycModule: CommonLendingKycModule): RequestEmailOtpUseCase {
        return commonLendingKycModule.provideRequestEmailOtpUseCase
    }

    @Provides
    @Singleton
    internal fun provideVerifyAadhaarOtpUseCase(commonLendingKycModule: CommonLendingKycModule): VerifyAadhaarOtpUseCase {
        return commonLendingKycModule.provideVerifyAadhaarOtpUseCase
    }

    @Provides
    @Singleton
    internal fun provideVerifyCreditReportOtpUseCase(commonLendingKycModule: CommonLendingKycModule): VerifyCreditReportOtpUseCase {
        return commonLendingKycModule.provideVerifyCreditReportOtpUseCase
    }

    @Provides
    @Singleton
    internal fun provideVerifyEmailOtpUseCase(commonLendingKycModule: CommonLendingKycModule): VerifyEmailOtpUseCase {
        return commonLendingKycModule.provideVerifyEmailOtpUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchVerifySelfieUseCase(commonLendingKycModule: CommonLendingKycModule): VerifySelfieUseCase {
        return commonLendingKycModule.provideFetchVerifySelfieUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchAadhaarCaptchaUseCase(commonLendingKycModule: CommonLendingKycModule): FetchAadhaarCaptchaUseCase {
        return commonLendingKycModule.provideFetchAadhaarCaptchaUseCase
    }

    @Provides
    @Singleton
    internal fun provideSavePanDetailsUseCase(commonLendingKycModule: CommonLendingKycModule): SavePanDetailsUseCase {
        return commonLendingKycModule.provideSavePanDetailsUseCase
    }

    @Provides
    @Singleton
    internal fun provideSaveAadhaarDetailsUseCase(commonLendingKycModule: CommonLendingKycModule): SaveAadhaarDetailsUseCase {
        return commonLendingKycModule.provideSaveAadhaarDetailsUseCase
    }

    @Provides
    @Singleton
    internal fun provideVerifyPanDetailsUseCase(commonLendingKycModule: CommonLendingKycModule): VerifyPanDetailsUseCase {
        return commonLendingKycModule.provideVerifyPanDetailsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchLendingKycFaqListUseCase(commonLendingKycModule: CommonLendingKycModule): FetchLendingKycFaqListUseCase {
        return commonLendingKycModule.provideFetchLendingKycFaqListUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchLendingKycFaqDetailsUseCase(commonLendingKycModule: CommonLendingKycModule): FetchLendingKycFaqDetailsUseCase {
        return commonLendingKycModule.provideFetchLendingKycFaqDetailsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchEmailDeliveryStatusUseCase(commonLendingKycModule: CommonLendingKycModule): FetchEmailDeliveryStatusUseCase {
        return commonLendingKycModule.provideFetchEmailDeliveryStatusUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchExperianConsentUseCase(commonLendingKycModule: CommonLendingKycModule): FetchExperianConsentUseCase {
        return commonLendingKycModule.provideFetchExperianConsentUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchVerifyAadhaarPanLinkageUseCase(commonLendingKycModule: CommonLendingKycModule): FetchVerifyAadhaarPanLinkageUseCase {
        return commonLendingKycModule.provideFetchVerifyAadhaarPanLinkageUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchExperianTermsAndConditionUseCase(commonLendingKycModule: CommonLendingKycModule): FetchExperianTermsAndConditionUseCase {
        return commonLendingKycModule.provideFetchExperianTermsAndConditionUseCase
    }

    @Provides
    @Singleton
    internal fun provideVerifyCreditReportOtpV2UseCase(commonLendingKycModule: CommonLendingKycModule): VerifyCreditReportOtpV2UseCase {
        return commonLendingKycModule.provideVerifyCreditReportOtpV2UseCase
    }

    @Provides
    @Singleton
    internal fun provideRequestCreditReportOtpV2UseCase(commonLendingKycModule: CommonLendingKycModule): RequestCreditReportOtpV2UseCase {
        return commonLendingKycModule.provideRequestCreditReportOtpV2UseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchDigiLockerScreenContentUseCase(commonLendingKycModule: CommonLendingKycModule): FetchDigiLockerScreenContentUseCase {
        return commonLendingKycModule.provideFetchDigiLockerScreenContentUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchDigiLockerRedirectionUrlUseCase(commonLendingKycModule: CommonLendingKycModule): FetchDigiLockerRedirectionUrlUseCase {
        return commonLendingKycModule.provideFetchDigiLockerRedirectionUrlUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchDigiLockerVerificationStatusUseCase(commonLendingKycModule: CommonLendingKycModule): FetchDigiLockerVerificationStatusUseCase {
        return commonLendingKycModule.provideFetchDigiLockerVerificationStatusUseCase
    }
    @Provides
    @Singleton
    internal fun provideUpdateDigiLockerRedirectionDataUseCase(commonLendingKycModule: CommonLendingKycModule): UpdateDigiLockerRedirectionDataUseCase {
        return commonLendingKycModule.provideUpdateDigiLockerRedirectionDataUseCase
    }
}