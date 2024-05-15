package com.jar.app.feature_onboarding.shared.ui.common_faq

import com.jar.app.core_base.domain.model.Faq
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchFaqStaticDataUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class CommonFaqViewModel constructor(
    private val fetchDashboardStaticContentUseCase: FetchFaqStaticDataUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _smsFaqListFlow =
        MutableStateFlow<RestClientResult<List<Faq>>>(RestClientResult.none())
    val smsFaqListFlow: CFlow<RestClientResult<List<Faq>>>
        get() = _smsFaqListFlow.shareIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        ).toCommonFlow()

    fun fetchFaqList(type: StaticContentType) {
        viewModelScope.launch {
            val list = ArrayList<Faq>()
            fetchDashboardStaticContentUseCase.fetchFaqStaticData(type)
                .collect(
                    onLoading = {
                        _smsFaqListFlow.emit(RestClientResult.loading())
                    },
                    onSuccess = {
                        it.smsFAQs?.faqDataList?.forEach { data ->
                            list.addAll(data.faqs.map { it.copy(type = data.type) })
                        }
                        _smsFaqListFlow.emit(RestClientResult.success(list))
                    },
                    onError = { errorMessage, _ ->
                        _smsFaqListFlow.emit(RestClientResult.error(errorMessage))
                    }
                )
        }
    }
}