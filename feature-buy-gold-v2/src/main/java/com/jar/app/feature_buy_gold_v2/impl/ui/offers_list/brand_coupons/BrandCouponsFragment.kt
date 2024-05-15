package com.jar.app.feature_buy_gold_v2.impl.ui.offers_list.brand_coupons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.dp
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.FragmentBrandCouponListingBinding
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.domain.event.RewardsEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@AndroidEntryPoint
internal class BrandCouponsFragment : BaseFragment<FragmentBrandCouponListingBinding>() {

    private val viewModel by viewModels<BrandCouponViewModel> { defaultViewModelProviderFactory }

    private var brandCouponAdapter: BrandCouponsAdapter? = null

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var job: Job? = null
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBrandCouponListingBinding
        get() = FragmentBrandCouponListingBinding::inflate

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        fetchAndDisplayBrandCoupons()

        setupListeners()
        observeLiveData()

    }

    private fun observeLiveData() {
        viewModel.brandCouponsLiveData.observe(viewLifecycleOwner) {
            if (it.status == RestClientResult.Status.SUCCESS) {
                if ((it.data?.data?.activeCoupons ?: 0) > 0) {
                    binding.activeCouponCount.text =
                        "${it.data?.data?.activeCoupons} ${it.data?.data?.activeCouponsDescription}"
                } else {
                    binding.activeCouponCount.isVisible = false
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                brandCouponAdapter?.loadStateFlow?.collectLatest { loadState ->
                    val isListEmpty =
                        loadState.refresh is LoadState.NotLoading && brandCouponAdapter?.itemCount == 0
                    if (isListEmpty) {
                        showNoCouponsView()

                    } else {
                        showCouponsView()
                    }

                }
            }
        }


        viewModel.brandCouponsPaginatedLiveData.observe(viewLifecycleOwner) { pagingData ->
            binding.brandCouponSwipeToRefresh.isRefreshing = false
            lifecycleScope.launch {
                brandCouponAdapter?.submitData(pagingData)
            }

        }
    }

    private fun showCouponsView() {
        binding.apply {
            couponGroup.isVisible = true
            emptyCouponsState.isVisible = false


        }
    }

    private fun showNoCouponsView() {
        binding.apply {
            couponGroup.isVisible = false
            emptyCouponsState.isVisible = true
            val emptyCouponResponse =
                viewModel.brandCouponsLiveData.value?.data?.data?.offersEmptyResponse
            brandCouponEmptyLabel.text = emptyCouponResponse?.header
            Glide.with(requireContext())
                .load(emptyCouponResponse?.image)
                .override(212.dp, 160.dp)
                .into(brandCouponEmptyImage)
            btnSpinToWin.setText(
                emptyCouponResponse?.spinCTA?.ctaText ?: getCustomString(MR.strings.spin_to_win)
            )
            tvSpinToWin.text = emptyCouponResponse?.spinCTA?.title

            btnSpinToWin.setDebounceClickListener {
                analyticsHandler.postEvent(
                    RewardsEventKey.ClickedButtonRewardsSection,
                    mapOf(
                        RewardsEventKey.CTA to RewardsEventKey.Go_to_Spin,
                        RewardsEventKey.Screen to RewardsEventKey.Offer_List
                    )
                )
                emptyCouponResponse?.spinCTA?.ctaDeeplink?.let { _deeplink ->
                    EventBus.getDefault().post(HandleDeepLinkEvent(_deeplink))
                }
            }
        }
    }

    private fun setupListeners() {
        binding.brandCouponSwipeToRefresh.setOnRefreshListener {
            fetchAndDisplayBrandCoupons()
        }

    }

    private fun setupUI() {
        setupBrandCouponRecyclerView()
    }

    private fun fetchAndDisplayBrandCoupons() {
        job?.cancel()
        job = lifecycleScope.launch {
            val tasks = listOf(
                async {
                    viewModel.fetchBrandCouponsWithPaging()
                },
                async {
                    viewModel.fetchBrandCouponInfo()
                }
            )

            // Wait for both tasks to complete
            tasks.awaitAll()
        }

    }

    private fun setupBrandCouponRecyclerView() {
        brandCouponAdapter = BrandCouponsAdapter() { brandCouponId ->

            analyticsHandler.postEvent(
                RewardsEventKey.ClickedButtonRewardsSection,
                mapOf(
                    RewardsEventKey.CTA to RewardsEventKey.Brand_Coupon,
                    RewardsEventKey.Screen to RewardsEventKey.Offer_List
                )
            )
            navigateTo("android-app://com.jar.app/couponDetailsPage/$brandCouponId", true)

        }
        binding.brandCouponRv.apply {
            layoutManager =
                StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL)
            adapter = brandCouponAdapter
        }
    }

}

private const val SPAN_COUNT = 2