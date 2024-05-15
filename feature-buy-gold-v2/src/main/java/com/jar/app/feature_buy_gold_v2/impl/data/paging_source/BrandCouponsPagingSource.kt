package com.jar.app.feature_buy_gold_v2.impl.data.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jar.app.feature_coupon_api.data.network.CouponDataSource
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.BrandsCouponInfo
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

private const val STARTING_PAGE_INDEX = 0

internal class BrandCouponsPagingSource  constructor(
    private val couponDataSource: CouponDataSource
) : PagingSource<Int, BrandsCouponInfo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BrandsCouponInfo> {
        val page = params.key ?: STARTING_PAGE_INDEX
        val result = couponDataSource.fetchBrandCoupons(
            page,
            params.loadSize
        )
        when (result.status) {
            RestClientResult.Status.SUCCESS -> {
                val brandCoupons = result.data?.data?.brandsCouponInfoList
                val nextKey = if (brandCoupons.isNullOrEmpty()) null else (page + 1)
                return LoadResult.Page(
                    data = brandCoupons.orEmpty(),
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

    override fun getRefreshKey(state: PagingState<Int, BrandsCouponInfo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}