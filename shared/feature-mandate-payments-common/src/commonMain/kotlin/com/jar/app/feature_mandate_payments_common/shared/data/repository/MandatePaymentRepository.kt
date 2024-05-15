package com.jar.app.feature_mandate_payments_common.shared.data.repository

import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiRequest
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.InitiateMandatePaymentApiResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.mandate_help.MandateEducationResp
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method.EnabledPaymentMethodResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_method.RecentlyUsedPaymentMethodData
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PreferredBankPageItem
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_upi.VerifyUpiAddressResponse
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface MandatePaymentRepository : BaseRepository {

    suspend fun verifyUpiAddress(
        upiAddress: String,
        isEligibleForMandate: Boolean
    ): Flow<RestClientResult<ApiResponseWrapper<VerifyUpiAddressResponse>>>

    suspend fun initiateMandatePayment(initiateAutoInvestRequest: InitiateMandatePaymentApiRequest?): Flow<RestClientResult<ApiResponseWrapper<InitiateMandatePaymentApiResponse?>>>

    suspend fun fetchMandatePaymentStatus(mandatePaymentResultFromSDK: MandatePaymentResultFromSDK): Flow<RestClientResult<ApiResponseWrapper<FetchMandatePaymentStatusResponse?>>>

    suspend fun fetchMandateEducation(mandateStaticContentType: MandatePaymentCommonConstants.MandateStaticContentType): Flow<RestClientResult<ApiResponseWrapper<MandateEducationResp>>>

    suspend fun fetchPreferredBank(): Flow<RestClientResult<ApiResponseWrapper<PreferredBankPageItem?>>>

    suspend fun fetchEnabledPaymentMethods(flowType: String?): Flow<RestClientResult<ApiResponseWrapper<EnabledPaymentMethodResponse?>>>

    suspend fun fetchRecentlyUsedPaymentMethods(flowType: String?): Flow<RestClientResult<ApiResponseWrapper<RecentlyUsedPaymentMethodData?>>>
}