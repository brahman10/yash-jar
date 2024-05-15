package com.jar.app.feature_buy_gold_v2.impl.ui.offers_list.jar_coupons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants.BuyGoldFlowContext.OFFERS_SECTION
import com.jar.app.feature_buy_gold_v2.databinding.FragmentJarCouponListingBinding
import com.jar.app.feature_buy_gold_v2.shared.domain.event.RewardsEventKey
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class JarCouponsFragment : BaseFragment<FragmentJarCouponListingBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModel by viewModels<JarCouponViewModel> { defaultViewModelProviderFactory }


    private var jarCouponAdapter: JarCouponsAdapter? = null
    private var couponDeepLinkBaseUrl: String? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentJarCouponListingBinding
        get() = FragmentJarCouponListingBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setupAppBar() {

    }


    private fun getData() {
        fetchCouponData()
    }

    private fun fetchCouponData() {
        viewModel.fetchJarCoupons(OFFERS_SECTION)
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()

    }

    private fun observeLiveData() {
        viewModel.couponCodesLiveData.observe(viewLifecycleOwner) {
            if (it.status == RestClientResult.Status.SUCCESS) {
                binding.jarCouponSwipeToRefresh.isRefreshing = false

                binding.activeCouponCount.text = "${it.data?.activeCoupons} ${it.data?.activeOffersDescription}"
                couponDeepLinkBaseUrl = it.data?.applyCouponDeepLink
                analyticsHandler.postEvent(
                    RewardsEventKey.Shown_rewards_Section,
                    mapOf(
                        RewardsEventKey.Tab to RewardsEventKey.Jar,
                        RewardsEventKey.Active_Coupons_Title to it?.data?.jarCouponInfoList?.joinToString { it.couponCode.orEmpty() }.orEmpty()
                    )
                )
                jarCouponAdapter?.submitList(it?.data?.jarCouponInfoList)
            }
        }
    }

    private fun setupListeners() {

        binding.jarCouponSwipeToRefresh.setOnRefreshListener {
            getData()
        }

    }

    private fun setupUI() {
        setupJarCouponRecyclerView()
    }

    private fun setupJarCouponRecyclerView() {
        jarCouponAdapter = JarCouponsAdapter(lifecycleScope) { jarCouponInfo ->
           val couponEndPoint =  "/" + jarCouponInfo.couponCode + "/" + jarCouponInfo.couponType
            analyticsHandler.postEvent(
                RewardsEventKey.ClickedButtonRewardsSection,
                mapOf(
                    RewardsEventKey.Tab to RewardsEventKey.Jar,
                    RewardsEventKey.CTA to RewardsEventKey.Jar_Coupon,
                    RewardsEventKey.Screen to RewardsEventKey.Offer_List,
                    RewardsEventKey.Coupon_Title_Clicked to jarCouponInfo.title.orEmpty()

                )
            )
            val deepLink = couponDeepLinkBaseUrl + couponEndPoint
            EventBus.getDefault().post(HandleDeepLinkEvent(deepLink))
        }
        binding.jarCouponRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = jarCouponAdapter
        }


    }

}