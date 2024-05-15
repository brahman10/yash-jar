package com.jar.app.feature_gold_lease.shared.ui

import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2MyOrders
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2UserLeaseItem
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseMyOrdersUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchUserLeasesUseCase
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GoldLeaseV2MyOrderViewModel constructor(
    private val fetchGoldLeaseMyOrdersUseCase: FetchGoldLeaseMyOrdersUseCase,
    private val fetchUserLeasesUseCase: FetchUserLeasesUseCase,
    coroutineScope: CoroutineScope?
) {

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
        private const val FILTER_ONGOING = "ONGOING"
        private const val FILTER_PAST = "PAST"
    }

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    var pagerPast: Pager<Int, GoldLeaseV2UserLeaseItem>? = null

    var pagerOngoing: Pager<Int, GoldLeaseV2UserLeaseItem>? = null

    private val _goldLeaseMyOrdersFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2MyOrders?>>>(
            RestClientResult.none()
        )
    val goldLeaseMyOrdersFlow: CStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2MyOrders?>>>
        get() = _goldLeaseMyOrdersFlow.toCommonStateFlow()

    private val _userLeaseOngoingListFlow = MutableStateFlow<PagingData<GoldLeaseV2UserLeaseItem>?>(null)
    val userLeaseOngoingListFlow: CStateFlow<PagingData<GoldLeaseV2UserLeaseItem>?>
        get() = _userLeaseOngoingListFlow.toCommonStateFlow()

    private val _userLeasePastListFlow = MutableStateFlow<PagingData<GoldLeaseV2UserLeaseItem>?>(null)
    val userLeasePastListFlow: CStateFlow<PagingData<GoldLeaseV2UserLeaseItem>?>
        get() = _userLeasePastListFlow.toCommonStateFlow()

    fun fetchUserLeaseOngoing() {
        viewModelScope.launch {
            pagerOngoing = Pager(
                viewModelScope,
                config = PagingConfig(
                    pageSize = NETWORK_PAGE_SIZE,
                    enablePlaceholders = false,
                    initialLoadSize = NETWORK_PAGE_SIZE
                ),
                initialKey = 0,
                getItems = { currentKey, size ->

                    val response = fetchUserLeasesUseCase.fetchUserLeases(
                        page = currentKey,
                        size = size,
                        userLeasesFilter = FILTER_ONGOING
                    )

                    val leasePlans = response.data?.data?.userLeasesList
                    val nextKey = if (leasePlans.isNullOrEmpty()) null else (currentKey + 1)

                    val items = response.data?.data?.userLeasesList.orEmpty()
                    PagingResult(
                        items = items,
                        currentKey = currentKey,
                        prevKey = { if (currentKey == 0) null else currentKey - 1 },
                        nextKey = { nextKey }
                    )
                },
            )
            pagerOngoing!!.pagingData.cachedIn(viewModelScope).collectLatest {
                _userLeaseOngoingListFlow.emit(it)
            }
        }
    }

    fun fetchUserLeasePast() {
        viewModelScope.launch {
            pagerPast = Pager(
                viewModelScope,
                config = PagingConfig(
                    pageSize = NETWORK_PAGE_SIZE,
                    enablePlaceholders = false,
                    initialLoadSize = NETWORK_PAGE_SIZE
                ),
                initialKey = 0,
                getItems = { currentKey, size ->

                    val response = fetchUserLeasesUseCase.fetchUserLeases(
                        page = currentKey,
                        size = size,
                        userLeasesFilter = FILTER_PAST
                    )

                    val leasePlans = response.data?.data?.userLeasesList
                    val nextKey = if (leasePlans.isNullOrEmpty()) null else (currentKey + 1)

                    val items = response.data?.data?.userLeasesList.orEmpty()
                    PagingResult(
                        items = items,
                        currentKey = currentKey,
                        prevKey = { if (currentKey == 0) null else currentKey - 1 },
                        nextKey = { nextKey }
                    )
                },
            )
            pagerPast!!.pagingData.cachedIn(viewModelScope).collectLatest {
                _userLeasePastListFlow.emit(it)
            }
        }
    }

    fun fetchMyOrdersDetails() {
        viewModelScope.launch {
            fetchGoldLeaseMyOrdersUseCase.fetchGoldLeaseV2MyOrders().collect {
                _goldLeaseMyOrdersFlow.emit(it)
            }
        }
    }
}