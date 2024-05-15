package com.jar.app.feature_lending_kyc.shared.domain.repository

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.data.network.LendingKycDataSource
import com.jar.app.feature_lending_kyc.shared.data.repository.LendingKycRepository
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarOtpRequest
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.model.DigilockerRedirectionData
import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaarRequest
import com.jar.app.feature_lending_kyc.shared.domain.model.VerifyAadhaarOtpRequest
import com.jar.app.feature_lending_kyc.shared.domain.model.VerifyOtpV2RequestData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

internal class LendingKycRepositoryImpl constructor(
    private val lendingKycDataSource: LendingKycDataSource
) : LendingKycRepository {

    override suspend fun requestEmailOtp(email: String, kycFeatureFlowType: KycFeatureFlowType) =
        getFlowResult {
            lendingKycDataSource.requestEmailOtp(email, kycFeatureFlowType)
        }

    override suspend fun verifyEmailOtp(
        email: String,
        otp: String,
        kycFeatureFlowType: KycFeatureFlowType
    ) = getFlowResult {
        lendingKycDataSource.verifyEmailOtp(email, otp, kycFeatureFlowType)
    }

    override suspend fun requestCreditReportOtp(kycFeatureFlowType: KycFeatureFlowType) = getFlowResult {
        lendingKycDataSource.requestCreditReportOtp(kycFeatureFlowType)
    }

    override suspend fun verifyCreditReportOtp(
        otp: String,
        kycFeatureFlowType: KycFeatureFlowType
    ) =
        getFlowResult {
            lendingKycDataSource.verifyCreditReportOtp(otp, kycFeatureFlowType)
        }

    override suspend fun requestCreditReportOtpV2(kycFeatureFlowType: KycFeatureFlowType) = getFlowResult {
        lendingKycDataSource.requestCreditReportOtpV2(kycFeatureFlowType)
    }

    override suspend fun verifyCreditReportOtpV2(
        requestData: VerifyOtpV2RequestData,
        kycFeatureFlowType: KycFeatureFlowType
    ) =
        getFlowResult {
            lendingKycDataSource.verifyCreditReportOtpV2(requestData, kycFeatureFlowType)
        }

    override suspend fun fetchJarVerifiedUserPan(kycFeatureFlowType: KycFeatureFlowType) = getFlowResult {
        lendingKycDataSource.fetchJarVerifiedUserPan(kycFeatureFlowType)
    }

    override suspend fun searchCKycDetails(kycAadhaarRequest: KycAadhaarRequest) = getFlowResult {
        lendingKycDataSource.searchCKycDetails(kycAadhaarRequest)
    }

    override suspend fun fetchKycAadhaarDetails() = getFlowResult {
        lendingKycDataSource.fetchKycAadhaarDetails()
    }

    override suspend fun fetchAadhaarCaptcha(kycFeatureFlowType: KycFeatureFlowType) = getFlowResult {
        lendingKycDataSource.fetchAadhaarCaptcha(kycFeatureFlowType)
    }

    override suspend fun requestAadhaarOtp(aadhaarOtpRequest: AadhaarOtpRequest, kycFeatureFlowType: KycFeatureFlowType) = getFlowResult {
        lendingKycDataSource.requestAadhaarOtp(aadhaarOtpRequest, kycFeatureFlowType)
    }

    override suspend fun verifyAadhaarOtp(verifyAadhaarOtpRequest: VerifyAadhaarOtpRequest, kycFeatureFlowType: KycFeatureFlowType) =
        getFlowResult {
            lendingKycDataSource.verifyAadhaarOtp(verifyAadhaarOtpRequest, kycFeatureFlowType)
        }

    override suspend fun verifySelfie(selfie: ByteArray, kycFeatureFlowType: KycFeatureFlowType, loanApplicationId: String?) =
        getFlowResult {
            lendingKycDataSource.verifySelfie(selfie, loanApplicationId, kycFeatureFlowType)
        }

    override suspend fun fetchKycProgress() = getFlowResult {
        lendingKycDataSource.fetchKycProgress()
    }

    override suspend fun savePanDetails(jsonObject: JsonObject, kycFeatureFlowType: KycFeatureFlowType) = getFlowResult {
        lendingKycDataSource.savePanDetails(jsonObject, kycFeatureFlowType)
    }

    override suspend fun saveAadhaarDetails(kycFeatureFlowType: KycFeatureFlowType) = getFlowResult {
        lendingKycDataSource.saveAadhaarDetails(kycFeatureFlowType)
    }

    override suspend fun verifyPanDetails(
        creditReportPAN: CreditReportPAN,
        kycFeatureFlowType: KycFeatureFlowType
    ) = getFlowResult {
        lendingKycDataSource.verifyPanDetails(creditReportPAN, kycFeatureFlowType)
    }

    override suspend fun fetchExperianTermsAndConditions(kycFeatureFlowType: KycFeatureFlowType) = getFlowResult {
        lendingKycDataSource.fetchExperianTermsAndCondition(kycFeatureFlowType)
    }

    override suspend fun fetchKycFaqList() = getFlowResult {
        lendingKycDataSource.fetchLendingKycFaqList()
    }

    override suspend fun fetchLendingKycFaqDetails(param: String) = getFlowResult {
        lendingKycDataSource.fetchLendingKycFaqDetails(param)
    }

    override suspend fun fetchExperianConsent(kycFeatureFlowType: KycFeatureFlowType) = getFlowResult {
        lendingKycDataSource.fetchExperianConsent(kycFeatureFlowType)
    }

    override suspend fun fetchEmailDeliveryStatus(
        email: String,
        msgId: String,
        kycFeatureFlowType: KycFeatureFlowType
    ) = getFlowResult {
        lendingKycDataSource.fetchEmailDeliveryStatus(email, msgId, kycFeatureFlowType)
    }

    override suspend fun verifyAadhaarPanLinkage(kycFeatureFlowType: KycFeatureFlowType) = getFlowResult {
        lendingKycDataSource.verifyAadhaarPanLinkage(kycFeatureFlowType)
    }

    override suspend fun fetchDigiLockerScreenContent(
        applicationId: String,
        kycFeatureFlowType: KycFeatureFlowType
    ) =
        getFlowResult {
            lendingKycDataSource.fetchDigiLockerScreenContent(applicationId,kycFeatureFlowType)
        }

    override suspend fun fetchRedirectionUrlForVerificationThroughDigiLocker(kycFeatureFlowType: KycFeatureFlowType,shouldEnablePinless: Boolean) =
        getFlowResult {
            lendingKycDataSource.fetchRedirectionUrlForVerificationThroughDigiLocker(kycFeatureFlowType,shouldEnablePinless)
        }

    override suspend fun fetchDigiLockerVerificationStatus(kycFeatureFlowType: KycFeatureFlowType,shouldEnablePinless: Boolean) =
        getFlowResult {
            lendingKycDataSource.fetchDigiLockerVerificationStatus(kycFeatureFlowType, shouldEnablePinless)
        }

    override suspend fun updateDigiLockerRedirectData(kycFeatureFlowType: KycFeatureFlowType, data: DigilockerRedirectionData) =
        getFlowResult {
            lendingKycDataSource.updateDigiLockerRedirectData(kycFeatureFlowType, data)
        }


}