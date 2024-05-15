package com.jar.app.feature_kyc.shared.ui.kyc_faq

import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.feature_kyc.shared.domain.model.Faq
import com.jar.app.feature_kyc.shared.domain.model.KycFaqResponse
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycFaqUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KycFaqViewModel constructor(
    private val fetchKycFaqUseCase: FetchKycFaqUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)


    private val _faqFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<KycFaqResponse?>>>(RestClientResult.none())
    val faqFlow: CStateFlow<RestClientResult<ApiResponseWrapper<KycFaqResponse?>>>
        get() = _faqFlow.toCommonStateFlow()

    fun fetchFaq() {
        viewModelScope.launch {
            fetchKycFaqUseCase.fetchKycFaq(StaticContentType.KYC_DETAILS.name)
                .collect {
                    _faqFlow.emit(it)
                }
        }
    }

    /**
     * To get common type of data so that {@see FaqAdapter} can be reused
     */
    suspend fun getFlattenedFaqData(): List<Faq> {
        return withContext(Dispatchers.IO) {
            val resultList = arrayListOf<Faq>()
            _faqFlow.value?.data?.data?.kycDetails?.faq?.faqDataList?.forEach { faqMetaData ->
                faqMetaData.faqs.forEach { faq ->
                    faq.type = faqMetaData.type
                    resultList.add(faq)
                }
            }
            resultList
        }
    }

}