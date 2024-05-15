package com.jar.app.feature_gold_sip.shared.data.network

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_gold_sip.shared.domain.model.EligibleForGoldSipData
import com.jar.app.feature_gold_sip.shared.domain.model.GoldSipIntroData
import com.jar.app.feature_gold_sip.shared.domain.model.GoldSipSetupInfo
import com.jar.app.feature_gold_sip.shared.domain.model.UpdateSipDetails
import com.jar.app.feature_gold_sip.shared.util.GoldSipConstants
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

internal class GoldSipDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchGoldSipIntro() =
        getResult<ApiResponseWrapper<GoldSipIntroData>> {
            client.get {
                url(GoldSipConstants.Endpoints.FETCH_GOLD_SIP_INTRO)
                parameter("contentType", BaseConstants.StaticContentType.UPDATE_SIP_ED_INFO.name)
            }
        }

    suspend fun fetchIsEligibleForGoldSip() =
        getResult<ApiResponseWrapper<EligibleForGoldSipData>> {
            client.get {
                url(GoldSipConstants.Endpoints.FETCH_IS_ELIGIBLE_FOR_GOLD_SIP)
                parameter("contentType", BaseConstants.StaticContentType.GOLD_SIP_INFO.name)
            }
        }

    suspend fun fetchGoldSipDetails() =
        getResult<ApiResponseWrapper<UserGoldSipDetails>> {
            client.get {
                url(GoldSipConstants.Endpoints.FETCH_GOLD_SIP_DETAILS)
            }
        }

    suspend fun updateGoldSipDetails(updateSipDetails: UpdateSipDetails) =
        getResult<ApiResponseWrapper<UserGoldSipDetails>> {
            client.post {
                url(GoldSipConstants.Endpoints.UPDATE_GOLD_SIP_DETAILS)
                setBody(updateSipDetails)
            }
        }

    suspend fun disableGoldSip() =
        getResult<ApiResponseWrapper<UserGoldSipDetails>> {
            client.get {
                url(GoldSipConstants.Endpoints.DISABLE_GOLD_SIP)
            }
        }

    suspend fun fetchGoldSipTypeSetupInfo(subscriptionType: String) =
        getResult<ApiResponseWrapper<GoldSipSetupInfo>> {
            client.get {
                url(GoldSipConstants.Endpoints.FETCH_GOLD_SIP_SETUP_INFO)
                parameter("subscriptionType", subscriptionType)
            }
        }
}