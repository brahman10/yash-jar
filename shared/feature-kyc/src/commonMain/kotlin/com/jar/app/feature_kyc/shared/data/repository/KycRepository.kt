package com.jar.app.feature_kyc.shared.data.repository

import com.jar.app.feature_kyc.shared.domain.model.*
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface KycRepository : BaseRepository {

    suspend fun fetchKycDetails(kycContext: String? = null): Flow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>

    suspend fun fetchKycFaq(param: String): Flow<RestClientResult<ApiResponseWrapper<KycFaqResponse?>>>

    suspend fun postManualKycRequest(manualKycRequest: ManualKycRequest,fetch: Boolean = false, kycContext: String? = null): Flow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>

    suspend fun fetchKycDocumentsList(): Flow<RestClientResult<ApiResponseWrapper<KycDocListResponse?>>>

    suspend fun postKycOcrRequest(
        docType: String,
        byteArray: ByteArray,
        isKyc: Boolean = false
    ): Flow<RestClientResult<ApiResponseWrapper<KycOcrResponse?>>>

    suspend fun postFaceMatchRequest(
        docType: String,
        byteArray: ByteArray,
    ): Flow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>

    suspend fun postPanOcrRequest(byteArray: ByteArray): Flow<RestClientResult<ApiResponseWrapper<KycPanOcrResponse?>>>
}