package com.jar.app.feature_buy_gold_v2.impl.data.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jar.app.feature_buy_gold_v2.shared.domain.model.AuspiciousDate
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchAuspiciousDatesUseCase
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

private const val STARTING_PAGE_INDEX = 0

internal class AuspiciousDayPagingSource constructor(
    private val fetchAuspiciousDatesUseCase: FetchAuspiciousDatesUseCase
) : PagingSource<Int, AuspiciousDate>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AuspiciousDate> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val result = fetchAuspiciousDatesUseCase.fetchAuspiciousDates(
            page,
            params.loadSize
        )
        when (result.status) {
            RestClientResult.Status.SUCCESS -> {
                val transactions = result.data?.data?.auspiciousDateList
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

    override fun getRefreshKey(state: PagingState<Int, AuspiciousDate>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}