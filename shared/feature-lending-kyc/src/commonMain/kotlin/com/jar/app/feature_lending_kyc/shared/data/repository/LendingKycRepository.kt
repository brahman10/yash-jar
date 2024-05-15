package com.jar.app.feature_lending_kyc.shared.data.repository

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.core_base.data.dto.KycProgressResponseDTO
import com.jar.app.feature_lending_kyc.shared.domain.model.*
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

interface LendingKycRepository : BaseRepository {

    suspend fun requestEmailOtp(email: String, kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<EmailOtp?>>>

    suspend fun verifyEmailOtp(
        email: String,
        otp: String,
        kycFeatureFlowType: KycFeatureFlowType
    ): Flow<RestClientResult<ApiResponseWrapper<EmailOtp?>>>

    suspend fun requestCreditReportOtp(kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<CreditReportOtp?>>>

    suspend fun verifyCreditReportOtp(
        otp: String,
        kycFeatureFlowType: KycFeatureFlowType
    ): Flow<RestClientResult<ApiResponseWrapper<CreditReportOtp?>>>

    suspend fun requestCreditReportOtpV2(featureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<CreditReportOtpResponseV2?>>>

    suspend fun verifyCreditReportOtpV2(requestData: VerifyOtpV2RequestData, kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<VerifyOtpResponseV2?>>>

    suspend fun fetchJarVerifiedUserPan(kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<CreditReportPAN?>>>

    suspend fun searchCKycDetails(kycAadhaarRequest: KycAadhaarRequest): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchKycAadhaarDetails(): Flow<RestClientResult<ApiResponseWrapper<KycAadhaar?>>>

    suspend fun fetchAadhaarCaptcha(kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<AadhaarCaptcha?>>>

    suspend fun requestAadhaarOtp(aadhaarOtpRequest: AadhaarOtpRequest, kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun verifyAadhaarOtp(verifyAadhaarOtpRequest: VerifyAadhaarOtpRequest, kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun verifySelfie(selfie: ByteArray, kycFeatureFlowType: KycFeatureFlowType, loanApplicationId: String? = null): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchKycProgress(): Flow<RestClientResult<ApiResponseWrapper<KycProgressResponseDTO?>>>

    suspend fun savePanDetails(jsonObject: JsonObject, kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun saveAadhaarDetails(kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun verifyPanDetails(
        creditReportPAN: CreditReportPAN,
        kycFeatureFlowType: KycFeatureFlowType
    ): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchExperianTermsAndConditions(kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<ExperianTnCResponse?>>>

    suspend fun fetchExperianConsent(kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<ExperianConsent?>>>

    suspend fun fetchKycFaqList(): Flow<RestClientResult<ApiResponseWrapper<FaqDetails?>>>

    suspend fun fetchLendingKycFaqDetails(param: String): Flow<RestClientResult<ApiResponseWrapper<FaqTypeDetails?>>>

    suspend fun fetchEmailDeliveryStatus(
        email: String,
        msgId: String,
        kycFeatureFlowType: KycFeatureFlowType
    ): Flow<RestClientResult<ApiResponseWrapper<EmailOtp?>>>

    suspend fun verifyAadhaarPanLinkage(kycFeatureFlowType: KycFeatureFlowType): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

    suspend fun fetchDigiLockerScreenContent(
        applicationId: String,
        kycFeatureFlowType: KycFeatureFlowType
    ): Flow<RestClientResult<ApiResponseWrapper<DigiLockerScreenContent?>>>

    suspend fun fetchRedirectionUrlForVerificationThroughDigiLocker(kycFeatureFlowType: KycFeatureFlowType,shouldEnablePinless: Boolean): Flow<RestClientResult<ApiResponseWrapper<DigiLockerRedirectionUrlData?>>>

    suspend fun fetchDigiLockerVerificationStatus(kycFeatureFlowType: KycFeatureFlowType,shouldEnablePinless: Boolean): Flow<RestClientResult<ApiResponseWrapper<DigiLockerVerificationStatus?>>>
    suspend fun updateDigiLockerRedirectData(kycFeatureFlowType: KycFeatureFlowType, data: DigilockerRedirectionData): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

}