package com.jar.app.feature_lending_kyc.shared.di

import com.jar.app.feature_lending_kyc.shared.api.impl.FetchKycProgressUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.api.use_case.FetchKycProgressUseCase
import com.jar.app.feature_lending_kyc.shared.data.network.LendingKycDataSource
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.repository.LendingKycRepositoryImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchAadhaarCaptchaUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerRedirectionUrlUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerScreenContentUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchDigiLockerVerificationStatusUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchEmailDeliveryStatusUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchExperianConsentUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchExperianTermsAndConditionUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchJarVerifiedUserPanUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchKycAadhaarDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchLendingKycFaqDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchLendingKycFaqListUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchVerifyAadhaarPanLinkageUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestAadhaarOtpUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestCreditReportOtpUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestCreditReportOtpV2UseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestEmailOtpUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.SaveAadhaarDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.SavePanDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.SearchCkycAadhaarDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.UpdateDigiLockerRedirectionDataUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyAadhaarOtpUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyCreditReportOtpUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyCreditReportOtpV2UseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyEmailOtpUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifyPanDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.VerifySelfieUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.FetchAadhaarCaptchaUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.FetchDigiLockerRedirectionUrlUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.FetchDigiLockerScreenContentUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.FetchDigiLockerVerificationStatusUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.FetchEmailDeliveryStatusUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.FetchExperianConsentUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.FetchExperianTermsAndConditionUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.FetchJarVerifiedUserPanUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.FetchKycAadhaarDetailsUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.FetchLendingKycFaqDetailsUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.FetchLendingLendingKycFaqListUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.FetchVerifyAadhaarPanLinkageUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.RequestAadhaarOtpUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.RequestCreditReportOtpUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.RequestCreditReportOtpV2UseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.RequestEmailOtpUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.SaveAadhaarDetailsUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.SavePanDetailsUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.SearchCkycAadhaarDetailsUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.UpdateDigiLockerRedirectionDataUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.VerifyAadhaarOtpUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.VerifyCreditReportOtpUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.VerifyCreditReportOtpV2UseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.VerifyEmailOtpUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.VerifyPanDetailsUseCaseImpl
import com.jar.app.feature_lending_kyc.shared.domain.use_case.impl.VerifySelfieUseCaseImpl
import io.ktor.client.HttpClient

class CommonLendingKycModule(client: HttpClient) {
    
    val lendingKycDataSource: LendingKycDataSource by lazy {
        LendingKycDataSource(client)
    }
    
    val lendingKycRepository: LendingKycRepository by lazy {
        LendingKycRepositoryImpl(lendingKycDataSource)
    }

    val provideFetchJarVerifiedUserPanUseCase: FetchJarVerifiedUserPanUseCase by lazy {
        FetchJarVerifiedUserPanUseCaseImpl(lendingKycRepository)
    }
    
    val provideSearchCKycAadhaarDetailsUseCase: SearchCkycAadhaarDetailsUseCase by lazy {
        SearchCkycAadhaarDetailsUseCaseImpl(lendingKycRepository)
    }
    
    val provideFetchKycAadhaarDetailsUseCase: FetchKycAadhaarDetailsUseCase by lazy {
        FetchKycAadhaarDetailsUseCaseImpl(lendingKycRepository)
    }

    val provideFetchKycProgressUseCase: FetchKycProgressUseCase by lazy {
        FetchKycProgressUseCaseImpl(lendingKycRepository)
    }
    
    val provideRequestAadhaarOtpUseCase: RequestAadhaarOtpUseCase by lazy {
        RequestAadhaarOtpUseCaseImpl(lendingKycRepository)
    }
    
    val provideRequestCreditReportOtpUseCase: RequestCreditReportOtpUseCase by lazy {
        RequestCreditReportOtpUseCaseImpl(lendingKycRepository)
    }
    
    val provideRequestEmailOtpUseCase: RequestEmailOtpUseCase by lazy {
        RequestEmailOtpUseCaseImpl(lendingKycRepository)
    }

    val provideVerifyAadhaarOtpUseCase: VerifyAadhaarOtpUseCase by lazy {
        VerifyAadhaarOtpUseCaseImpl(lendingKycRepository)
    }

    val provideVerifyCreditReportOtpUseCase: VerifyCreditReportOtpUseCase by lazy {
        VerifyCreditReportOtpUseCaseImpl(lendingKycRepository)
    }

    val provideVerifyEmailOtpUseCase: VerifyEmailOtpUseCase by lazy {
        VerifyEmailOtpUseCaseImpl(lendingKycRepository)
    }

    val provideFetchVerifySelfieUseCase: VerifySelfieUseCase by lazy {
        VerifySelfieUseCaseImpl(lendingKycRepository)
    }

    val provideFetchAadhaarCaptchaUseCase: FetchAadhaarCaptchaUseCase by lazy {
        FetchAadhaarCaptchaUseCaseImpl(lendingKycRepository)
    }

    val provideSavePanDetailsUseCase: SavePanDetailsUseCase by lazy {
        SavePanDetailsUseCaseImpl(lendingKycRepository)
    }

    val provideSaveAadhaarDetailsUseCase: SaveAadhaarDetailsUseCase by lazy {
        SaveAadhaarDetailsUseCaseImpl(lendingKycRepository)
    }

    val provideVerifyPanDetailsUseCase: VerifyPanDetailsUseCase by lazy {
        VerifyPanDetailsUseCaseImpl(lendingKycRepository)
    }

    val provideFetchLendingKycFaqListUseCase: FetchLendingKycFaqListUseCase by lazy {
        FetchLendingLendingKycFaqListUseCaseImpl(lendingKycRepository)
    }

    val provideFetchLendingKycFaqDetailsUseCase: FetchLendingKycFaqDetailsUseCase by lazy {
        FetchLendingKycFaqDetailsUseCaseImpl(lendingKycRepository)
    }

    val provideFetchEmailDeliveryStatusUseCase: FetchEmailDeliveryStatusUseCase by lazy {
        FetchEmailDeliveryStatusUseCaseImpl(lendingKycRepository)
    }

    val provideFetchExperianConsentUseCase: FetchExperianConsentUseCase by lazy {
        FetchExperianConsentUseCaseImpl(lendingKycRepository)
    }

    val provideFetchVerifyAadhaarPanLinkageUseCase: FetchVerifyAadhaarPanLinkageUseCase by lazy {
        FetchVerifyAadhaarPanLinkageUseCaseImpl(lendingKycRepository)
    }

    val provideFetchExperianTermsAndConditionUseCase: FetchExperianTermsAndConditionUseCase by lazy {
        FetchExperianTermsAndConditionUseCaseImpl(lendingKycRepository)
    }

    val provideVerifyCreditReportOtpV2UseCase: VerifyCreditReportOtpV2UseCase by lazy {
        VerifyCreditReportOtpV2UseCaseImpl(lendingKycRepository)
    }

    val provideRequestCreditReportOtpV2UseCase: RequestCreditReportOtpV2UseCase by lazy {
        RequestCreditReportOtpV2UseCaseImpl(lendingKycRepository)
    }

    val provideFetchDigiLockerScreenContentUseCase: FetchDigiLockerScreenContentUseCase by lazy {
        FetchDigiLockerScreenContentUseCaseImpl(lendingKycRepository)
    }

    val provideFetchDigiLockerRedirectionUrlUseCase: FetchDigiLockerRedirectionUrlUseCase by lazy {
        FetchDigiLockerRedirectionUrlUseCaseImpl(lendingKycRepository)
    }

    val provideFetchDigiLockerVerificationStatusUseCase: FetchDigiLockerVerificationStatusUseCase by lazy {
        FetchDigiLockerVerificationStatusUseCaseImpl(lendingKycRepository)
    }
    val provideUpdateDigiLockerRedirectionDataUseCase: UpdateDigiLockerRedirectionDataUseCase by lazy {
        UpdateDigiLockerRedirectionDataUseCaseImpl(lendingKycRepository)
    }
}