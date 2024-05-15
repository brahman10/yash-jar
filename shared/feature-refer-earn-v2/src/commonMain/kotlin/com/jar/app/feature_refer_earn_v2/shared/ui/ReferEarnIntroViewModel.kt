package com.jar.app.feature_refer_earn_v2.shared.ui

import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralIntroStaticDataUseCase
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralsShareMessageUseCase
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralsUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.util.checkIfAnyRestClientIsLoading
import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReferEarnIntroViewModel constructor(
    private val fetchReferralIntroStaticDataUseCase: FetchReferralIntroStaticDataUseCase,
    private val fetchReferralsUseCase: FetchReferralsUseCase,
    private val fetchReferralsShareMessageUseCase: FetchReferralsShareMessageUseCase,
    private val coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }

    private val _uiStateFlow = MutableStateFlow<ReferEarnIntroStateData>(ReferEarnIntroStateData())
    val uiStateFlow: CStateFlow<ReferEarnIntroStateData>
        get() = _uiStateFlow.asStateFlow().toCommonStateFlow()

    private val _combinedFlowLoading = _uiStateFlow.transform {
        emit(checkIfAnyRestClientIsLoading(it.introScreenData))
    }.toCommonFlow()
    val combinedFlowLoading: CFlow<Boolean> = _combinedFlowLoading.toCommonFlow()

    fun fetchReferEarnIntros() {
        viewModelScope.launch {
            fetchReferralIntroStaticDataUseCase.fetchReferralIntroStaticData().collect { data ->
                _uiStateFlow.update { it.copy(introScreenData = data) }
            }
        }
    }

    fun fetchReferEarnMsgLinks(appsFlyerInviteLink: String) {
        viewModelScope.launch {
            fetchReferralsShareMessageUseCase.fetchReferralShareMessage(appsFlyerInviteLink)
                .collect { data ->
                    _uiStateFlow.update { it.copy(shareMessageDetails = data) }
                }
        }
    }

    val pager = Pager(
        viewModelScope,
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            initialLoadSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = false
        ),
        initialKey = 0,
        getItems = { currentKey, size ->
            val response = fetchReferralsUseCase.fetchReferrals(
                currentKey,
                size
            )
            val items = response.data?.data?.referrals.orEmpty()
            PagingResult(
                items = items,
                currentKey = currentKey,
                prevKey = { currentKey - 1 },
                nextKey = { currentKey + 1 }
            )
        },
    )

    val pagingData = pager.pagingData.cachedIn(viewModelScope).toCommonFlow()

}
