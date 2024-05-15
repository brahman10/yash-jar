package com.jar.app.feature_transaction.shared.ui

import com.jar.app.core_base.domain.mapper.toGoldBalance
import com.jar.app.core_base.domain.model.GoldBalance
import com.jar.app.core_base.util.orFalse
import com.jar.app.feature_transaction.shared.domain.model.FilterValueData
import com.jar.app.feature_transaction.shared.domain.model.IndividualFilterObject
import com.jar.app.feature_transaction.shared.domain.model.TransactionData
import com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest
import com.jar.app.feature_transaction.shared.domain.model.UserGoldDetailsRes
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchTransactionListingUseCase
import com.jar.app.feature_transaction.shared.domain.use_case.IFetchUserGoldDetailsUseCase
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GoldTransactionViewModel constructor(
    private val fetchUserGoldDetailsUseCase: IFetchUserGoldDetailsUseCase,
    private val fetchTransactionListingUseCase: IFetchTransactionListingUseCase,
    private val fetchUserGoldBalanceUseCase: FetchUserGoldBalanceUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    companion object {
        private const val NETWORK_PAGE_SIZE = 10
    }

    private val _userGoldLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<UserGoldDetailsRes?>>>(RestClientResult.none())
    val userGoldLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<UserGoldDetailsRes?>>>
        get() = _userGoldLiveData.toCommonStateFlow()

    private val _goldBalanceLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<GoldBalance?>>>(RestClientResult.none())
    val goldBalanceLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<GoldBalance?>>>
        get() = _goldBalanceLiveData.toCommonStateFlow()

    private val _goldTransactionFlow = MutableStateFlow<PagingData<TransactionData>?>(null)
    val goldTransactionFlow: CFlow<PagingData<TransactionData>?>
        get() = _goldTransactionFlow.toCommonFlow()

    var pager: Pager<Int, TransactionData>? = null

    fun fetchUserGoldDetails() {
        viewModelScope.launch {
            fetchUserGoldDetailsUseCase.fetchUserGoldDetails().collect {
                _userGoldLiveData.emit(it)
            }
        }
    }

    fun fetchTransactions(
        filterList: List<FilterValueData>? = null,
        dateFilter: Pair<Long, Long>? = null
    ) {
        viewModelScope.launch {
            val individualFilterObjectList = getGroupedFilters(filterList)

            pager = Pager(
                viewModelScope,
                config = PagingConfig(
                    pageSize = NETWORK_PAGE_SIZE,
                    enablePlaceholders = false,
                    initialLoadSize = NETWORK_PAGE_SIZE
                ),
                initialKey = 0,
                getItems = { currentKey, size ->
                    val transactionListingRequest = TransactionListingRequest(
                        filtersUsed = individualFilterObjectList?.isNotEmpty()
                            .orFalse() || dateFilter != null,
                        individualFilterObject = individualFilterObjectList,
                        startDate = dateFilter?.first,
                        endDate = dateFilter?.second,
                    )
                    val request = transactionListingRequest.copy(
                        pageNumber = currentKey,
                        pageSize = size
                    )

                    val response = fetchTransactionListingUseCase.fetchTransactionListing(request)
                    val items = response.data?.data.orEmpty()
                    PagingResult(
                        items = items,
                        currentKey = currentKey,
                        prevKey = { currentKey - 1 },
                        nextKey = { currentKey + 1 }
                    )
                },
            )
            pager!!.pagingData.cachedIn(viewModelScope).collectLatest {
                _goldTransactionFlow.emit(it)
            }
        }
    }

    private suspend fun getGroupedFilters(list: List<FilterValueData>?): List<IndividualFilterObject>? {
        return withContext(Dispatchers.Default) {
            if (list.isNullOrEmpty())
                null
            else {
                val resultList = ArrayList<IndividualFilterObject>()
                list.groupBy { it.keyName }.forEach { mapEntry ->
                    resultList.add(
                        IndividualFilterObject(
                            mapEntry.key,
                            mapEntry.value.map { it.name })
                    )
                }
                resultList
            }
        }
    }

    fun fetchUserGoldBalance() {
        viewModelScope.launch {
            fetchUserGoldBalanceUseCase.fetchUserGoldBalance().mapToDTO { it?.toGoldBalance() }
                .collect {
                    _goldBalanceLiveData.emit(it)
                }
        }
    }
}

