package com.jar.app.feature_gold_lease.impl.ui.lease_plans

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jar.app.feature_gold_lease.shared.domain.model.LeasePlanList
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeasePlansUseCase
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

private const val STARTING_PAGE_INDEX = 0

internal class GoldLeaseV2PlansPagingSource constructor(
    private val fetchGoldLeasePlansUseCase: FetchGoldLeasePlansUseCase,
    private val leasePlanListingFilter: String
): PagingSource<Int, LeasePlanList>() {

    override fun getRefreshKey(state: PagingState<Int, LeasePlanList>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LeasePlanList> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val result = fetchGoldLeasePlansUseCase.fetchGoldLeasePlans(
            leasePlanListingFilter = leasePlanListingFilter,
            pageNo = page,
            pageSize = params.loadSize
        )
        when (result.status) {
            RestClientResult.Status.SUCCESS -> {
                val leasePlans = result.data?.data?.leasePlansList
                val nextKey = if (leasePlans.isNullOrEmpty()) null else (page + 1)
                return LoadResult.Page(
                    data = leasePlans.orEmpty(),
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
}