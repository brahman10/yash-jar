package com.jar.app.feature_kyc.shared.domain.repository

import com.jar.app.feature_kyc.shared.data.network.KycDataSource
import com.jar.app.feature_kyc.shared.data.repository.KycRepository
import com.jar.app.feature_kyc.shared.domain.model.ManualKycRequest

internal class KycRepositoryImpl constructor(
    private val kycDataSource: KycDataSource
) : KycRepository {

    override suspend fun fetchKycDetails(kycContext: String?) = getFlowResult {
        kycDataSource.fetchKYCDetails(kycContext)
    }

    override suspend fun fetchKycFaq(param: String) = getFlowResult {
        kycDataSource.fetchKycFaq(param)
    }

    override suspend fun postManualKycRequest(
        manualKycRequest: ManualKycRequest,
        fetch: Boolean,
        kycContext: String?
    ) =
        getFlowResult {
            kycDataSource.postManualKycRequest(manualKycRequest, fetch, kycContext)
        }

    override suspend fun fetchKycDocumentsList() = getFlowResult {
        kycDataSource.fetchKycDocumentsList()
    }

    override suspend fun postKycOcrRequest(
        docType: String,
        byteArray: ByteArray,
        isKyc: Boolean
    ) = getFlowResult {
        kycDataSource.postKycOcrRequest(docType, byteArray, isKyc)
    }

    override suspend fun postFaceMatchRequest(
        docType: String,
        byteArray: ByteArray
    ) =
        getFlowResult {
            kycDataSource.postFaceMatchRequest(docType, byteArray)
        }

    override suspend fun postPanOcrRequest(
        byteArray: ByteArray
    ) = getFlowResult {
        kycDataSource.postPanOcrResponse(byteArray)
    }
}