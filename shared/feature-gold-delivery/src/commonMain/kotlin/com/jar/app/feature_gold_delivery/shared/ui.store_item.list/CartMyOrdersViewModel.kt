package com.jar.app.feature_gold_delivery.shared.ui.store_item.list

import com.jar.app.feature_gold_delivery.shared.domain.model.ProductsV2
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchTransactionListingUseCase
import com.jar.app.feature_gold_delivery.shared.domain.use_case.GetAllStoreItemsUseCase
import com.jar.app.feature_transaction.shared.domain.model.IndividualFilterObject
import com.jar.app.feature_transaction.shared.domain.model.TransactionData
import com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import com.kuuurt.paging.multiplatform.insertPagingSeparators
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class CartMyOrdersFragmentViewModel constructor(
    private val transactionListingUseCase: FetchTransactionListingUseCase,
    private val getAllStoreItemsUseCase: GetAllStoreItemsUseCase,
    coroutineScope: CoroutineScope?,
) {
    var if24HourAdded = false
    var lastMonthAdded = false

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)
    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }

    private val _storeItemsLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>(RestClientResult.none())
    val storeItemsLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<ProductsV2?>>>
        get() = _storeItemsLiveData.toCommonStateFlow()

    private fun isIn24Hours(body: TransactionData?): Boolean {
        return body?.orderedInLast24Hrs == true
    }
    val pager = Pager(
        viewModelScope,
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = false,
            initialLoadSize = NETWORK_PAGE_SIZE
        ),
        initialKey = 0,
        getItems = { currentKey, size ->
            val data = TransactionListingRequest(
                filtersUsed = true,
                individualFilterObject = getFilter(),
                startDate = null,
                endDate = null,
                pageNumber = currentKey,
                pageSize = size,
            )

            val response = transactionListingUseCase.fetchTransactionListing(
                data
            )
            val items = response.data?.data.orEmpty().map { MyOrdersData.MyOrdersBody(it) as MyOrdersData }
            PagingResult(
                items = items,
                currentKey = currentKey,
                prevKey = { currentKey - 1 },
                nextKey = { currentKey + 1 }
            )
        },
    )

    val pagingData = pager.pagingData
        .map {
            it.insertPagingSeparators { before, after ->
                if (after == null && before is MyOrdersData.MyOrdersBody && isIn24Hours(before.body)) {
                    // first item
                    if (!if24HourAdded) {
                        if24HourAdded = true
                        MyOrdersData.MyOrdersHeader("Last 24 Hour")
                    } else {
                        return@insertPagingSeparators null
                    }
                } else if (before is MyOrdersData.MyOrdersBody && isIn24Hours(before?.body)) {
                    // do nothing as already added
                    return@insertPagingSeparators null
                } else if (!lastMonthAdded && before != null) {
                    lastMonthAdded = true
                    MyOrdersData.MyOrdersHeader("Previous Orders")
                } else {
                    // do nothing let it add
                    return@insertPagingSeparators null
                }
            }
        }
        .cachedIn(viewModelScope).toCommonFlow()
    private fun getFilter(): List<IndividualFilterObject> {
        return listOf(
            IndividualFilterObject(
                filterEnums = "TRANSACTION_TYPE",
                subFilters = listOf("ASSET_DELIVERY")
            )
        )
    }

    fun fetchProducts() {
        viewModelScope.launch {
            getAllStoreItemsUseCase.getAllStoreItems(null).collect {
                _storeItemsLiveData.emit(it)
            }
        }
    }
}
suspend fun <T : Any> PagingData<T>.insertSeparators(predicate: suspend (before: T, after: T) -> T?): PagingData<T> {
    val list = (this@insertSeparators as List<T>).toMutableList()
    val listIterator = list.listIterator()

    var previous: T? = null
    while (listIterator.hasNext()) {
        previous = listIterator.previous()
        val next = listIterator.next()

        val header = predicate(previous, next)
        header?.let {
            listIterator.add(it)
        }
    }
    return list as PagingData<T>
}
