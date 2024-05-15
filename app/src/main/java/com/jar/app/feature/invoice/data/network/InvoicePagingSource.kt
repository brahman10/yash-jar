package com.jar.app.feature.invoice.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.invoice.domain.model.Invoice
import com.jar.app.feature.invoice.domain.use_case.FetchInvoiceUseCase

private const val STARTING_PAGE_INDEX = 0

class InvoicePagingSource constructor(
    private val fetchInvoiceUseCase: FetchInvoiceUseCase
) : PagingSource<Int, Invoice>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Invoice> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val result = fetchInvoiceUseCase.fetchInvoice(
            page,
            params.loadSize
        )
        when (result.status) {
            RestClientResult.Status.SUCCESS -> {
                val transactions = result.data?.data?.invoice
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

    override fun getRefreshKey(state: PagingState<Int, Invoice>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}