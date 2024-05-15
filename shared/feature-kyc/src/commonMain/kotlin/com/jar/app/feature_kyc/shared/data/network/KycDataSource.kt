package com.jar.app.feature_kyc.shared.data.network

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_kyc.shared.domain.model.*
import com.jar.app.feature_kyc.shared.util.KycConstants
import com.jar.app.feature_kyc.shared.util.KycConstants.Endpoints
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class KycDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchKYCDetails(kycContext: String?) =
        getResult<ApiResponseWrapper<KYCStatusDetails?>> {
            client.get {
                url(Endpoints.FETCH_KYC_DETAILS)
                if (kycContext.isNullOrBlank().not())
                    parameter("kycContext", kycContext)
            }
        }

    suspend fun fetchKycFaq(param: String) =
        getResult<ApiResponseWrapper<KycFaqResponse?>> {
            client.get {
                url(Endpoints.FETCH_KYC_FAQ)
                parameter("contentType", param)
            }
        }

    suspend fun postManualKycRequest(
        manualKycRequest: ManualKycRequest,
        fetch: Boolean,
        kycContext: String?
    ) =
        getResult<ApiResponseWrapper<KYCStatusDetails?>> {
            client.post {
                url(Endpoints.POST_MANUAL_KYC_REQUEST)
                parameter("fetch", fetch)
                parameter("docType", BaseConstants.PAN)
                parameter("kycContext", kycContext)
                setBody(manualKycRequest)
            }
        }

    suspend fun fetchKycDocumentsList() =
        getResult<ApiResponseWrapper<KycDocListResponse?>> {
            client.get {
                url(Endpoints.FETCH_DOCUMENT_LIST)
                parameter("param", KycConstants.KYC_DOC_LIST_PARAM)
            }
        }

    suspend fun postKycOcrRequest(
        docType: String,
        byteArray: ByteArray,
        isKyc: Boolean = false
    ) = getResult<ApiResponseWrapper<KycOcrResponse?>> {
        client.post {
            url(Endpoints.POST_OCR_REQUEST)
            parameter("docType", docType)
            parameter("kyc", isKyc)
            setBody(MultiPartFormDataContent(
                formData {
                    append(
                        BaseConstants.PART,
                        byteArray,
                        Headers.build {
                            append(
                                HttpHeaders.ContentType,
                                "image/*"
                            )
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=kyc_doc"
                            )
                            append(HttpHeaders.ContentLength, "${byteArray.size}")
                        }
                    )
                }
            ))
        }
    }

    suspend fun postPanOcrResponse(byteArray: ByteArray) =
        getResult<ApiResponseWrapper<KycPanOcrResponse?>> {
            client.post {
                url(Endpoints.POST_PAN_OCR_REQUEST)
                setBody(MultiPartFormDataContent(
                    formData {
                        append(
                            BaseConstants.PART,
                            byteArray,
                            Headers.build {
                                append(
                                    HttpHeaders.ContentType,
                                    "image/*"
                                )
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=pan_card"
                                )
                                append(HttpHeaders.ContentLength, "${byteArray.size}")
                            }
                        )
                    }
                ))
            }
        }

    suspend fun postFaceMatchRequest(docType: String, byteArray: ByteArray) =
        getResult<ApiResponseWrapper<KYCStatusDetails?>> {
            client.post {
                url(Endpoints.POST_FACE_MATCH_REQUEST)
                parameter("docType", docType)
                setBody(MultiPartFormDataContent(
                    formData {
                        append(
                            BaseConstants.SELFIE,
                            byteArray,
                            Headers.build {
                                append(
                                    HttpHeaders.ContentType,
                                    "image/*"
                                )
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=selfie"
                                )
                                append(HttpHeaders.ContentLength, "${byteArray.size}")
                            }
                        )
                    }
                ))
            }
        }
}