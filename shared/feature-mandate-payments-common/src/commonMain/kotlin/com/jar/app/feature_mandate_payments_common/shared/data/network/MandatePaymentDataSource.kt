package com.jar.app.feature_mandate_payments_common.shared.data.network

import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method.RecentlyUsedPaymentMethodData
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.mandate_help.MandateEducationResp
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method.EnabledPaymentMethodResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PreferredBankPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_upi.VerifyUpiAddressResponse
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*

class MandatePaymentDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun verifyUpiAddress(
        vpa: String,
        isEligibleForMandate: Boolean
    ) = getResult<ApiResponseWrapper<VerifyUpiAddressResponse>> {
        client.post {
            url(MandatePaymentCommonConstants.Endpoints.VERIFY_VPA)
            parameter("vpa", vpa)
            parameter("isEligibleForMandate", isEligibleForMandate)
        }
    }

    suspend fun initiateMandatePayment(initiateAutoInvestRequest: InitiateMandatePaymentApiRequest?) =
        getResult<ApiResponseWrapper<InitiateMandatePaymentApiResponse?>> {
            client.post {
                url(MandatePaymentCommonConstants.Endpoints.INITIATE_MANDATE_PAYMENT)
                setBody(initiateAutoInvestRequest)
            }
        }

    suspend fun fetchMandatePaymentStatus(mandatePaymentResultFromSDK: MandatePaymentResultFromSDK) =
        getResult<ApiResponseWrapper<FetchMandatePaymentStatusResponse?>> {
            client.post {
                url(MandatePaymentCommonConstants.Endpoints.FETCH_MANDATE_PAYMENT_STATUS)
                setBody(mandatePaymentResultFromSDK)
            }
        }

    suspend fun fetchMandateEducation(mandateStaticContentType: MandatePaymentCommonConstants.MandateStaticContentType) =
        getResult<ApiResponseWrapper<MandateEducationResp>> {
            client.get {
                url(MandatePaymentCommonConstants.Endpoints.FETCH_MANDATE_EDUCATION)
                parameter("contentType", mandateStaticContentType.name)
            }
        }

    suspend fun fetchPreferredBank() =
        getResult<ApiResponseWrapper<PreferredBankPageItem?>> {
            client.get {
                url(MandatePaymentCommonConstants.Endpoints.FETCH_PREFERRED_BANK)
            }
        }

    suspend fun fetchEnabledPaymentMethods(flowType: String?) =
        getResult<ApiResponseWrapper<EnabledPaymentMethodResponse?>> {
            client.get {
                url(MandatePaymentCommonConstants.Endpoints.FETCH_ENABLED_PAYMENT_METHODS)
                if (flowType.isNullOrBlank().not())
                    parameter("flowType", flowType)
            }
        }

    suspend fun fetchRecentlyUsedPaymentMethods(flowType: String?) =
        getResult<ApiResponseWrapper<RecentlyUsedPaymentMethodData?>> {
            client.get {
                url(MandatePaymentCommonConstants.Endpoints.FETCH_RECENTLY_USED_PAYMENT_METHODS)
                if(flowType.isNullOrBlank().not())
                    parameter("flowType", flowType)
            }
        }
}