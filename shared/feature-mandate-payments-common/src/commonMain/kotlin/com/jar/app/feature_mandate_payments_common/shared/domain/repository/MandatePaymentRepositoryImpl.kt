package com.jar.app.feature_mandate_payments_common.shared.domain.repository

import com.jar.app.feature_mandate_payments_common.shared.data.network.MandatePaymentDataSource
import com.jar.app.feature_mandate_payments_common.shared.data.repository.MandatePaymentRepository
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants

class MandatePaymentRepositoryImpl constructor(
    private val mandatePaymentDataSource: MandatePaymentDataSource
) : MandatePaymentRepository {

    override suspend fun verifyUpiAddress(
        upiAddress: String,
        isEligibleForMandate: Boolean
    ) = getFlowResult {
        mandatePaymentDataSource.verifyUpiAddress(upiAddress, isEligibleForMandate)
    }

    override suspend fun initiateMandatePayment(initiateAutoInvestRequest: InitiateMandatePaymentApiRequest?) =
        getFlowResult {
            mandatePaymentDataSource.initiateMandatePayment(initiateAutoInvestRequest)
        }

    override suspend fun fetchMandatePaymentStatus(mandatePaymentResultFromSDK: MandatePaymentResultFromSDK) =
        getFlowResult {
            mandatePaymentDataSource.fetchMandatePaymentStatus(mandatePaymentResultFromSDK)
        }

    override suspend fun fetchMandateEducation(mandateStaticContentType: MandatePaymentCommonConstants.MandateStaticContentType) =
        getFlowResult {
            mandatePaymentDataSource.fetchMandateEducation(mandateStaticContentType)
        }

    override suspend fun fetchPreferredBank() =
        getFlowResult {
            mandatePaymentDataSource.fetchPreferredBank()
        }

    override suspend fun fetchEnabledPaymentMethods(flowType: String?) = getFlowResult {
        mandatePaymentDataSource.fetchEnabledPaymentMethods(flowType)
    }

    override suspend fun fetchRecentlyUsedPaymentMethods(flowType: String?) = getFlowResult {
        mandatePaymentDataSource.fetchRecentlyUsedPaymentMethods(flowType)
    }
}