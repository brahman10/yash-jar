package com.jar.app.feature_spends_tracker.impl.data.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_spends_tracker.shared.domain.model.spends_transaction_data.SpendsTransactionData
import com.jar.app.feature_spends_tracker.shared.domain.usecase.FetchSpendsTransactionDataUseCase

private const val STARTING_PAGE_INDEX = 0

internal class SpendsTransactionDataPagingSource constructor(
    private val fetchSpendsTransactionDataUseCase: FetchSpendsTransactionDataUseCase
) : PagingSource<Int, SpendsTransactionData>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SpendsTransactionData> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val result = fetchSpendsTransactionDataUseCase.fetchSpendsTransactionData(
            page,
            params.loadSize
        )
        when (result.status) {
            RestClientResult.Status.SUCCESS -> {
                val transactionData = result.data?.data
                val nextKey = if (transactionData.isNullOrEmpty()) null else (page + 1)
                return LoadResult.Page(
                    data = transactionData.orEmpty(),
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

    override fun getRefreshKey(state: PagingState<Int, SpendsTransactionData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}