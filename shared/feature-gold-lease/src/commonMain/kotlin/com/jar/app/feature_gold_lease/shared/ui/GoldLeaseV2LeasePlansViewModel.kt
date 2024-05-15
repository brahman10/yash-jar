package com.jar.app.feature_gold_lease.shared.ui

import com.jar.app.core_base.domain.mapper.toGoldBalance
import com.jar.app.core_base.domain.model.GoldBalance
import com.jar.app.core_base.util.orFalse
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2Filters
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2JewellerListing
import com.jar.app.feature_gold_lease.shared.domain.model.LeasePlanFilterInfoList
import com.jar.app.feature_gold_lease.shared.domain.model.LeasePlanList
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseJewellerListingsUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeasePlanFiltersUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeasePlansUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


class GoldLeaseV2LeasePlansViewModel constructor(
    private val fetchGoldLeaseJewellerListingsUseCase: FetchGoldLeaseJewellerListingsUseCase,
    private val fetchUserGoldBalanceUseCase: FetchUserGoldBalanceUseCase,
    private val fetchGoldLeasePlanFiltersUseCase: FetchGoldLeasePlanFiltersUseCase,
    private val fetchGoldLeasePlansUseCase: FetchGoldLeasePlansUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }

    private val _jewellerListingsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2JewellerListing?>>>(
            RestClientResult.none()
        )
    val jewellerListingsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2JewellerListing?>>>
        get() = _jewellerListingsFlow.toCommonStateFlow()

    private val _goldBalanceFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldBalance?>>>(RestClientResult.none())
    val goldBalanceFlow: CStateFlow<RestClientResult<ApiResponseWrapper<GoldBalance?>>>
        get() = _goldBalanceFlow.toCommonStateFlow()

    private val _goldLeaseFiltersListFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2Filters?>>>(RestClientResult.none())
    val goldLeaseFiltersListFlow: CStateFlow<RestClientResult<ApiResponseWrapper<GoldLeaseV2Filters?>>>
        get() = _goldLeaseFiltersListFlow.toCommonStateFlow()

    private val _goldLeasePlansFlow = MutableSharedFlow<PagingData<LeasePlanList>>()
    val goldLeasePlansFlow: CFlow<PagingData<LeasePlanList>>
        get() = _goldLeasePlansFlow.toCommonFlow()


    private var filterList: List<LeasePlanFilterInfoList>? = null

    private val _goldBalanceAndFiltersFlow =
        MutableStateFlow<Pair<GoldBalance, GoldLeaseV2Filters>?>(null)
    val goldBalanceAndFiltersFlow: CStateFlow<Pair<GoldBalance, GoldLeaseV2Filters>?>
        get() = _goldBalanceAndFiltersFlow.toCommonStateFlow()


    var pager: Pager<Int, LeasePlanList>? = null

    init {
        viewModelScope.launch {
            _goldBalanceFlow.combine(_goldLeaseFiltersListFlow) { goldBalance, goldLeaseV2Filter ->
                val goldBalanceData = goldBalance.data?.data
                val filterList = goldLeaseV2Filter.data?.data
                if (goldBalanceData != null && filterList != null) {
                    Pair(goldBalanceData, filterList)
                } else {
                    null
                }
            }.collectLatest {
                _goldBalanceAndFiltersFlow.emit(it)
            }
        }
    }

    fun fetchGoldLeasePlans(filter: String) {
        viewModelScope.launch {
            pager = Pager(
                viewModelScope,
                config = PagingConfig(
                    pageSize = NETWORK_PAGE_SIZE,
                    enablePlaceholders = false,
                    initialLoadSize = NETWORK_PAGE_SIZE
                ),
                initialKey = 0,
                getItems = { currentKey, size ->

                    val response = fetchGoldLeasePlansUseCase.fetchGoldLeasePlans(
                        leasePlanListingFilter = filter,
                        pageNo = currentKey,
                        pageSize = size,
                    )

                    val leasePlans = response.data?.data?.leasePlansList.orEmpty()

                    PagingResult(
                        items = leasePlans,
                        currentKey = currentKey,
                        prevKey = { currentKey - 1 },
                        nextKey = { currentKey + 2 }
                    )
                },
            )
            pager!!.pagingData.cachedIn(viewModelScope).collectLatest {
                _goldLeasePlansFlow.emit(it)
            }
        }
    }

    fun fetchJewellerListings() {
        viewModelScope.launch {
            fetchGoldLeaseJewellerListingsUseCase.fetchGoldLeaseJewellerListings().collect {
                _jewellerListingsFlow.emit(it)
            }
        }
    }

    fun fetchUserGoldBalance() {
        viewModelScope.launch {
            fetchUserGoldBalanceUseCase.fetchUserGoldBalance()
                .mapToDTO {
                    it?.toGoldBalance()
                }
                .collect {
                    _goldBalanceFlow.emit(it)
                }
        }
    }

    fun fetchGoldLeaseFiltersList() {
        viewModelScope.launch {
            fetchGoldLeasePlanFiltersUseCase.fetchGoldLeasePlanFilters().collect {
                filterList = it.data?.data?.leasePlanFilterInfoList
                filterList?.find { it.defaultFilter.orFalse() }?.let { defaultFilter ->
                    updateSelectedFilter(defaultFilter)
                } ?: kotlin.run {
                    _goldLeaseFiltersListFlow.emit(it)
                }
            }
        }
    }

    fun updateSelectedFilter(selectedFilter: LeasePlanFilterInfoList) {
        viewModelScope.launch {
            filterList?.let {
                val newFilterList = it.map { filterItem ->
                    filterItem.copy(
                        isSelected = selectedFilter.leasePlanListingFilterName == filterItem.leasePlanListingFilterName
                    )
                }
                _goldLeaseFiltersListFlow.emit(
                    RestClientResult.success(
                        ApiResponseWrapper(
                            GoldLeaseV2Filters(
                                leasePlanFilterInfoList = newFilterList
                            ),
                            success = true
                        )
                    )
                )
            }
        }
    }
}