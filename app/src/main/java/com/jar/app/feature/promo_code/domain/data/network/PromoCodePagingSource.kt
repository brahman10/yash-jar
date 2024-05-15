package com.jar.app.feature.promo_code.domain.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature.promo_code.domain.data.PromoCode
import com.jar.app.feature.promo_code.domain.use_case.FetchPromoCodeUseCase

private const val STARTING_PAGE_INDEX = 0

class PromoCodePagingSource constructor(
    private val fetchPromoCodeUseCase: FetchPromoCodeUseCase
) : PagingSource<Int, PromoCode>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PromoCode> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val result = fetchPromoCodeUseCase.fetchPromoCode(
            page,
            params.loadSize
        )
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

    override fun getRefreshKey(state: PagingState<Int, PromoCode>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}