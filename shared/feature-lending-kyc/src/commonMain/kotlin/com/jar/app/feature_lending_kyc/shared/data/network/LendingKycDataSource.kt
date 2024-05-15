package com.jar.app.feature_lending_kyc.shared.data.network

import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.core_base.data.dto.KycProgressResponseDTO
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarCaptcha
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarOtpRequest
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportOtp
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportOtpResponseV2
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.model.DigiLockerRedirectionUrlData
import com.jar.app.feature_lending_kyc.shared.domain.model.DigiLockerScreenContent
import com.jar.app.feature_lending_kyc.shared.domain.model.DigiLockerVerificationStatus
import com.jar.app.feature_lending_kyc.shared.domain.model.DigilockerRedirectionData
import com.jar.app.feature_lending_kyc.shared.domain.model.EmailOtp
import com.jar.app.feature_lending_kyc.shared.domain.model.ExperianConsent
import com.jar.app.feature_lending_kyc.shared.domain.model.ExperianTnCResponse
import com.jar.app.feature_lending_kyc.shared.domain.model.FaqDetails
import com.jar.app.feature_lending_kyc.shared.domain.model.FaqTypeDetails
import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaar
import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaarRequest
import com.jar.app.feature_lending_kyc.shared.domain.model.VerifyAadhaarOtpRequest
import com.jar.app.feature_lending_kyc.shared.domain.model.VerifyOtpResponseV2
import com.jar.app.feature_lending_kyc.shared.domain.model.VerifyOtpV2RequestData
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.DIGILOCKER_STRING
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants.Endpoints
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.JsonObject

class LendingKycDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun requestEmailOtp(email: String, kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<EmailOtp?>> {
            client.post {
                url(Endpoints.REQUEST_EMAIL_OTP)
                parameter("email", email)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun verifyEmailOtp(email: String, otp: String, kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<EmailOtp?>> {
            client.post {
                url(Endpoints.VERIFY_EMAIL_OTP)
                parameter("email", email)
                parameter("otp", otp)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun requestCreditReportOtp(kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<CreditReportOtp?>> {
            client.get {
                url(Endpoints.REQUEST_CREDIT_REPORT_OTP)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun verifyCreditReportOtp(otp: String, kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<CreditReportOtp?>> {
            client.get {
                url(Endpoints.VERIFY_CREDIT_REPORT_OTP)
                parameter("otp", otp)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun requestCreditReportOtpV2(kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<CreditReportOtpResponseV2?>> {
            client.get {
                url(Endpoints.REQUEST_CREDIT_REPORT_OTP_V2)
                parameter("type", "EXPERIAN")
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun verifyCreditReportOtpV2(
        data: VerifyOtpV2RequestData, kycFeatureFlowType: KycFeatureFlowType
    ) =
        getResult<ApiResponseWrapper<VerifyOtpResponseV2?>> {
            client.post {
                url(Endpoints.VERIFY_CREDIT_REPORT_OTP_V2)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
                setBody(data)
            }
        }

    suspend fun fetchJarVerifiedUserPan(kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<CreditReportPAN?>> {
            client.get {
                url(Endpoints.FETCH_JAR_VERIFIED_USER_PAN)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun searchCKycDetails(kycAadhaarRequest: KycAadhaarRequest) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.get {
                url(Endpoints.SEARCH_KYC_DETAILS)
            }
        }

    suspend fun fetchKycAadhaarDetails() =
        getResult<ApiResponseWrapper<KycAadhaar?>> {
            client.get {
                url(Endpoints.FETCH_KYC_AADHAAR_DETAILS)
            }
        }

    suspend fun fetchAadhaarCaptcha(kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<AadhaarCaptcha?>> {
            client.get {
                url(Endpoints.FETCH_AADHAAR_CAPTCHA)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun requestAadhaarOtp(aadhaarOtpRequest: AadhaarOtpRequest, kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.REQUEST_AADHAAR_OTP)
                setBody(aadhaarOtpRequest)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun verifyAadhaarOtp(
        verifyAadhaarOtpRequest: VerifyAadhaarOtpRequest,
        kycFeatureFlowType: KycFeatureFlowType
    ) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.VERIFY_AADHAAR_OTP)
                setBody(verifyAadhaarOtpRequest)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun verifySelfie(selfie: ByteArray, loanApplicationId: String? = null, kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.VERIFY_SELFIE)
                loanApplicationId?.let {
                    parameter("applicationId", it)
                }
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
                setBody(MultiPartFormDataContent(
                    formData {
                        append(
                            BaseConstants.SELFIE,
                            selfie,
                            Headers.build {
                                append(
                                    HttpHeaders.ContentType,
                                    "image/*"
                                )
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=selfie"
                                )
                                append(HttpHeaders.ContentLength, "${selfie.size}")
                            }
                        )
                    }
                ))
            }
        }

    suspend fun fetchKycProgress() =
        getResult<ApiResponseWrapper<KycProgressResponseDTO?>> {
            client.get {
                url(Endpoints.FETCH_KYC_PROGRESS)
            }
        }

    suspend fun savePanDetails(jsonObject: JsonObject, kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.SAVE_PAN_DETAILS)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
                setBody(jsonObject)
            }
        }

    suspend fun verifyPanDetails(creditReportPAN: CreditReportPAN, kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.VERIFY_PAN_DETAILS)
                setBody(creditReportPAN)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun saveAadhaarDetails(kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.SAVE_AADHAAR_DETAILS)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun fetchExperianTermsAndCondition(kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<ExperianTnCResponse?>> {
            client.get {
                url(Endpoints.FETCH_EXPERIAN_T_N_C)
                parameter("contentType", StaticContentType.EXPERIAN_TNC.name)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun fetchLendingKycFaqList() =
        getResult<ApiResponseWrapper<FaqDetails?>> {
            client.get {
                url(Endpoints.FETCH_LENDING_KYC_FAQ_LIST)
                parameter("contentType", StaticContentType.KYC_FAQ_LIST.name)
            }
        }

    suspend fun fetchLendingKycFaqDetails(param: String) =
        getResult<ApiResponseWrapper<FaqTypeDetails?>> {
            client.get {
                url(Endpoints.FETCH_LENDING_KYC_FAQ_DETAIL)
                parameter("contentType", param)
            }
        }

    suspend fun fetchExperianConsent(kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<ExperianConsent?>> {
            client.get {
                url(Endpoints.FETCH_LENDING_KYC_FAQ_DETAIL)
                parameter("contentType", StaticContentType.EXPERIAN_CONSENT.name)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun fetchEmailDeliveryStatus(
        email: String,
        msgId: String,
        kycFeatureFlowType: KycFeatureFlowType
    ) =
        getResult<ApiResponseWrapper<EmailOtp?>> {
            client.post {
                url(Endpoints.FETCH_EMAIL_DELIVERY_STATUS)
                parameter("email", email)
                parameter("msgId", msgId)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun verifyAadhaarPanLinkage(kycFeatureFlowType: KycFeatureFlowType) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.get {
                url(Endpoints.VERIFY_AADHAAR_PAN_LINKAGE)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun fetchDigiLockerScreenContent(
        applicationId: String,
        kycFeatureFlowType: KycFeatureFlowType
    ) =
        getResult<ApiResponseWrapper<DigiLockerScreenContent?>> {
            client.get {
                url(Endpoints.FETCH_DIGILOCKER_SCREEN_CONTENT)
                parameter("type", DIGILOCKER_STRING)
                parameter("applicationId", applicationId)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun fetchRedirectionUrlForVerificationThroughDigiLocker(kycFeatureFlowType: KycFeatureFlowType,shouldEnablePinless: Boolean) =
        getResult<ApiResponseWrapper<DigiLockerRedirectionUrlData?>> {
            client.get {
                url(
                    if (shouldEnablePinless)
                        Endpoints.FETCH_DIGILOCKER_VERIFICATION_URL_V2
                    else Endpoints.FETCH_DIGILOCKER_VERIFICATION_URL
                )
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun fetchDigiLockerVerificationStatus(kycFeatureFlowType: KycFeatureFlowType,shouldEnablePinless: Boolean) =
        getResult<ApiResponseWrapper<DigiLockerVerificationStatus?>> {
            client.get {
                url(
                    if (shouldEnablePinless)
                        Endpoints.FETCH_DIGILOCKER_VERIFICATION_STATUS_V2
                    else
                        Endpoints.FETCH_DIGILOCKER_VERIFICATION_STATUS
                )
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }

    suspend fun updateDigiLockerRedirectData(kycFeatureFlowType: KycFeatureFlowType, data: DigilockerRedirectionData) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.UPDATE_DIGILOCKER_REDIRECT_DATA)
                setBody(data)
                parameter("kycFeatureFlowType", kycFeatureFlowType.name)
            }
        }
}