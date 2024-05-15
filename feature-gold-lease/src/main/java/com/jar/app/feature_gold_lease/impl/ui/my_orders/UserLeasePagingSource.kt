package com.jar.app.feature_gold_lease.impl.ui.my_orders

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2UserLeaseItem
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchUserLeasesUseCase
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

private const val STARTING_PAGE_INDEX = 0
private const val FILTER_ONGOING = "ONGOING"
private const val FILTER_PAST = "PAST"

internal class UserLeasePagingSource constructor(
    private val fetchUserLeasesUseCase: FetchUserLeasesUseCase
): PagingSource<Int, GoldLeaseV2UserLeaseItem>() {

    var isOngoingFinished = false

    override fun getRefreshKey(state: PagingState<Int, GoldLeaseV2UserLeaseItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GoldLeaseV2UserLeaseItem> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val result = fetchUserLeasesUseCase.fetchUserLeases(
            page = page,
            size = params.loadSize,
            userLeasesFilter = if (isOngoingFinished.not()) FILTER_ONGOING else FILTER_PAST
        )
        when (result.status) {
            RestClientResult.Status.SUCCESS -> {
                val leasePlans = result.data?.data?.userLeasesList
                val nextKey = if (leasePlans.isNullOrEmpty() && isOngoingFinished.not())
                    0
                else if(leasePlans.isNullOrEmpty() && isOngoingFinished)
                    null
                else (page + 1)

                if (isOngoingFinished.not() && params.key != null) {
                    isOngoingFinished = leasePlans.isNullOrEmpty()
                }

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

    override val keyReuseSupported: Boolean
        get() = true
}