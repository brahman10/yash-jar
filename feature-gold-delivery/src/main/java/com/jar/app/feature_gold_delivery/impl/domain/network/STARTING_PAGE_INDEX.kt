package com.jar.app.feature_gold_delivery.impl.domain.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jar.app.feature_transaction.shared.domain.model.TransactionData
import com.jar.app.feature_transaction.shared.domain.model.TransactionListingRequest
import com.jar.app.feature_gold_delivery.shared.domain.use_case.FetchTransactionListingUseCase
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

private const val STARTING_PAGE_INDEX = 0

class TransactionListingPagingSource constructor(
    private val fetchTransactionListingUseCase: FetchTransactionListingUseCase,
    private val transactionListingRequest: TransactionListingRequest
) : PagingSource<Int, TransactionData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionData> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val request = transactionListingRequest.copy(
            pageNumber = page,
            pageSize = params.loadSize
        )
        val result = fetchTransactionListingUseCase.fetchTransactionListing(request)
        when (result.status) {
            RestClientResult.Status.SUCCESS -> {
                val transactions = result.data?.data
                val nextKey = if (transactions.isNullOrEmpty()) null else (page + 1)
                return LoadResult.Page(
                    data = transactions.orEmpty(),
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                    nextKey = nextKey
                )
            }

            RestClientResult.Status.ERROR -> {
                return LoadResult.Error(Throwable(result.message))
            }

            RestClientResult.Status.LOADING -> {
                throw Exception("Unsupported, This should not happen")
            }

            RestClientResult.Status.NONE -> {
                throw Exception("Unsupported, This should not happen")
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TransactionData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}