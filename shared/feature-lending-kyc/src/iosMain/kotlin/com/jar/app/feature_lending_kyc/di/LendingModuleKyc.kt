package com.jar.app.feature_lending_kyc.di

import com.jar.app.feature_lending_kyc.shared.api.impl.FetchKycProgressUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.api.use_case.FetchKycProgressUseCase
import com.jar.app.feature_lending_kyc.shared.data.network.LendingKycDataSource
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.repository.LendingKycRepositoryImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.*
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.*
import io.ktor.client.HttpClient


class LendingKycModule(
    client: HttpClient
) {

    private val lendingKycDataSource: LendingKycDataSource by lazy {
        LendingKycDataSource(client)
    }

    private val lendingKycRepository: LendingKycRepository by lazy {
        LendingKycRepositoryImpl(lendingKycDataSource)
    }


   fun provideFetchJarVerifiedUserPanUseCase(): FetchJarVerifiedUserPanUseCase {
        return FetchJarVerifiedUserPanUseCaseImpl(lendingKycRepository)
    }



   fun provideSearchCKycAadhaarDetailsUseCase(): SearchCkycAadhaarDetailsUseCase {
        return SearchCkycAadhaarDetailsUseCaseImpl(lendingKycRepository)
    }



   fun provideFetchKycAadhaarDetailsUseCase(): FetchKycAadhaarDetailsUseCase {
        return FetchKycAadhaarDetailsUseCaseImpl(lendingKycRepository)
    }



   fun provideFetchKycProgressUseCase(): FetchKycProgressUseCase {
        return FetchKycProgressUseCaseImpl(lendingKycRepository)
    }



   fun provideRequestAadhaarOtpUseCase(): RequestAadhaarOtpUseCase {
        return RequestAadhaarOtpUseCaseImpl(lendingKycRepository)
    }



   fun provideRequestCreditReportOtpUseCase(): RequestCreditReportOtpUseCase {
        return RequestCreditReportOtpUseCaseImpl(lendingKycRepository)
    }



   fun provideRequestEmailOtpUseCase(): RequestEmailOtpUseCase {
        return RequestEmailOtpUseCaseImpl(lendingKycRepository)
    }



   fun provideVerifyAadhaarOtpUseCase(): VerifyAadhaarOtpUseCase {
        return VerifyAadhaarOtpUseCaseImpl(lendingKycRepository)
    }



   fun provideVerifyCreditReportOtpUseCase(): VerifyCreditReportOtpUseCase {
        return VerifyCreditReportOtpUseCaseImpl(lendingKycRepository)
    }



   fun provideVerifyEmailOtpUseCase(): VerifyEmailOtpUseCase {
        return VerifyEmailOtpUseCaseImpl(lendingKycRepository)
    }



   fun provideFetchVerifySelfieUseCase(): VerifySelfieUseCase {
        return VerifySelfieUseCaseImpl(lendingKycRepository)
    }



   fun provideFetchAadhaarCaptchaUseCase(): FetchAadhaarCaptchaUseCase {
        return FetchAadhaarCaptchaUseCaseImpl(lendingKycRepository)
    }



   fun provideSavePanDetailsUseCase(): SavePanDetailsUseCase {
        return SavePanDetailsUseCaseImpl(lendingKycRepository)
    }



   fun provideSaveAadhaarDetailsUseCase(): SaveAadhaarDetailsUseCase {
        return SaveAadhaarDetailsUseCaseImpl(lendingKycRepository)
    }



   fun provideVerifyPanDetailsUseCase(): VerifyPanDetailsUseCase {
        return VerifyPanDetailsUseCaseImpl(lendingKycRepository)
    }



   fun provideFetchLendingKycFaqListUseCase(): FetchLendingKycFaqListUseCase {
        return FetchLendingLendingKycFaqListUseCaseImpl(lendingKycRepository)
    }



   fun provideFetchLendingKycFaqDetailsUseCase(): FetchLendingKycFaqDetailsUseCase {
        return FetchLendingKycFaqDetailsUseCaseImpl(lendingKycRepository)
    }



   fun provideFetchEmailDeliveryStatusUseCase(): FetchEmailDeliveryStatusUseCase {
        return FetchEmailDeliveryStatusUseCaseImpl(lendingKycRepository)
    }



   fun provideFetchExperianConsentUseCase(): FetchExperianConsentUseCase {
        return FetchExperianConsentUseCaseImpl(lendingKycRepository)
    }



   fun provideFetchVerifyAadhaarPanLinkageUseCase(): FetchVerifyAadhaarPanLinkageUseCase {
        return FetchVerifyAadhaarPanLinkageUseCaseImpl(lendingKycRepository)
    }



   fun provideFetchExperianTermsAndConditionUseCase(): FetchExperianTermsAndConditionUseCase {
        return FetchExperianTermsAndConditionUseCaseImpl(lendingKycRepository)
    }



   fun provideVerifyCreditReportOtpV2UseCase(): VerifyCreditReportOtpV2UseCase {
        return VerifyCreditReportOtpV2UseCaseImpl(lendingKycRepository)
    }


   fun provideRequestCreditReportOtpV2UseCase(): RequestCreditReportOtpV2UseCase {
        return RequestCreditReportOtpV2UseCaseImpl(lendingKycRepository)
    }
}