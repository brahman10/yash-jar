package com.jar.app.feature_gold_lease.impl.ui.my_orders

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_base.util.BaseAppDeeplink
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideTopToReveal
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.BaseItemDecoration
import com.jar.app.core_ui.item_decoration.HeaderItemDecoration
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeaseV2MyOrdersBinding
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2MyOrders
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2UserLeaseItem
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseConstants
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseEventKey
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class GoldLeaseV2MyOrdersFragment : BaseFragment<FragmentGoldLeaseV2MyOrdersBinding>(){

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    companion object {
        private const val EXTRA_FLOW_TYPE = "EXTRA_FLOW_TYPE"

        fun newInstance(flowType: String): GoldLeaseV2MyOrdersFragment {
            val fragment = GoldLeaseV2MyOrdersFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_FLOW_TYPE, flowType)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var userLeaseOngoingAdapter: UserLeaseAdapter? = null
    private var userLeasePastAdapter: UserLeaseAdapter? = null
    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 8.dp)

    private val headerItemDecorationOngoingLease =
        HeaderItemDecoration(object :
            BaseItemDecoration.SectionCallback {
            override fun isItemDecorationSection(position: Int): Boolean {
                return if (userLeaseOngoingAdapter?.snapshot()?.items.isNullOrEmpty()) {
                    false
                } else {
                    position == 0
                }
            }

            override fun getItemDecorationLayoutRes(position: Int): Int {
                return R.layout.cell_user_lease_header
            }

            override fun bindItemDecorationData(view: View, position: Int) {
                val header = view.findViewById<AppCompatTextView>(R.id.tvHeader)
                val title = userLeaseOngoingAdapter?.snapshot()?.items?.getOrNull(position)?.header
                header.isVisible = title != null
                if (title != null)
                    header.setHtmlText(title)
            }
        }, 0f)

    private val headerItemDecorationPastLease =
        HeaderItemDecoration(object :
            BaseItemDecoration.SectionCallback {
            override fun isItemDecorationSection(position: Int): Boolean {
                return if (userLeasePastAdapter?.snapshot()?.items.isNullOrEmpty()) {
                    false
                } else {
                    position == 0
                }
            }

            override fun getItemDecorationLayoutRes(position: Int): Int {
                return R.layout.cell_user_lease_header
            }

            override fun bindItemDecorationData(view: View, position: Int) {
                val header = view.findViewById<AppCompatTextView>(R.id.tvHeader)
                val title = userLeasePastAdapter?.snapshot()?.items?.getOrNull(position)?.header
                header.isVisible = title != null
                if (title != null)
                    header.setHtmlText(title)
            }
        }, 0f)

    private val flowTypeFromBundle by lazy {
        requireArguments().getString(EXTRA_FLOW_TYPE)
    }

    private val viewModelProvider by viewModels<GoldLeaseV2MyOrderViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeaseV2MyOrdersBinding
        get() = FragmentGoldLeaseV2MyOrdersBinding::inflate

    private val lottieAnimator = object : Animator.AnimatorListener{
        override fun onAnimationStart(animation: Animator) {
            binding.tvMyOrdersTodayEarnings.isInvisible = true
        }
        override fun onAnimationEnd(animation: Animator) {
            binding.tvMyOrdersTodayEarnings.slideTopToReveal(binding.tvMyOrdersTodayEarnings)
        }
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvMyOrdersTodayEarnings.isVisible = false
        userLeaseOngoingAdapter = UserLeaseAdapter {
            onLeaseClicked(it)
        }

        userLeasePastAdapter = UserLeaseAdapter {
            onLeaseClicked(it)
        }

        binding.rvUserLeasesOngoing.adapter = userLeaseOngoingAdapter
        binding.rvUserLeasesOngoing.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUserLeasesOngoing.addItemDecorationIfNoneAdded(spaceItemDecoration, headerItemDecorationOngoingLease)

        binding.rvUserLeasesPast.adapter = userLeasePastAdapter
        binding.rvUserLeasesPast.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUserLeasesPast.addItemDecorationIfNoneAdded(spaceItemDecoration, headerItemDecorationPastLease)
    }

    private fun onLeaseClicked(goldLeaseV2UserLeaseItem: GoldLeaseV2UserLeaseItem) {
        analyticsApi.postEvent(
            GoldLeaseEventKey.LeaseMyOrdersScreen.Lease_InfoScreenMyOrdersClicked,
            mapOf(
                GoldLeaseEventKey.Properties.FROM_FLOW to getFlowType(),
                GoldLeaseEventKey.Properties.BUTTON_TYPE to GoldLeaseEventKey.Values.LEASE_CARD_CHEVRON
            )
        )
        analyticsApi.postEvent(
            GoldLeaseEventKey.LeaseMyOrdersScreen.Lease_InfoScreenOngoingLeasesCardClicked,
            mapOf(
                GoldLeaseEventKey.Properties.FROM_FLOW to getFlowType(),
                GoldLeaseEventKey.Properties.LEASE_STATUS to goldLeaseV2UserLeaseItem.getUserLeaseStatus()?.name.orEmpty(),
                GoldLeaseEventKey.Properties.LEASE_TITLE to goldLeaseV2UserLeaseItem.jewellerName.orEmpty(),
                GoldLeaseEventKey.Properties.YEARLY_EARNINGS to goldLeaseV2UserLeaseItem.earningsPercentageComponent?.value.orEmpty(),
                GoldLeaseEventKey.Properties.LEASED_GOLD to goldLeaseV2UserLeaseItem.leasedGoldComponent?.value.orEmpty()
            )
        )
        navigateTo(
            "${BaseAppDeeplink.GoldLease.GOLD_LEASE_USER_LEASE_DETAILS_SCREEN}/${getFlowType()}/${goldLeaseV2UserLeaseItem.leaseId.orEmpty()}"
        )
    }

    private fun setupListeners() {
        binding.btnStartNewLease.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.LeaseMyOrdersScreen.Lease_InfoScreenMyOrdersClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to getFlowType(),
                    GoldLeaseEventKey.Properties.BUTTON_TYPE to GoldLeaseEventKey.Values.START_NEW_LEASE
                )
            )
            navigateTo(
                "${BaseAppDeeplink.GoldLease.GOLD_LEASE_PLANS_SCREEN}/${getFlowType()}/false"
            )
        }
    }

    private fun getFlowType() = flowTypeFromBundle ?: BaseConstants.GoldLeaseFlowType.HOME_CARD

    private fun getData() {
        viewModel.fetchMyOrdersDetails()
        viewModel.fetchUserLeaseOngoing()
        viewModel.fetchUserLeasePast()
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root)

        observeMyOrdersLiveData(weakReference)
        observeUserLeaseOngoing()
        observeUserLeasePast()
    }

    private fun observeUserLeasePast() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userLeasePastListFlow.collectLatest {
                    it?.let { pagingData ->
                        userLeasePastAdapter?.submitData(pagingData)
                    }
                }
            }
        }
    }

    private fun observeUserLeaseOngoing() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userLeaseOngoingListFlow.collectLatest {
                    it?.let { pagingData ->
                        userLeaseOngoingAdapter?.submitData(pagingData)
                    }
                }
            }
        }
    }

    private fun observeMyOrdersLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.goldLeaseMyOrdersFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setupMyOrdersDataInUI(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun setupMyOrdersDataInUI(goldLeaseV2MyOrders: GoldLeaseV2MyOrders) {
        binding.tvMyOrdersTitle.setHtmlText(goldLeaseV2MyOrders.mainTitle.orEmpty())
        binding.tvMyOrdersEarnings.text = getString(
            R.string.feature_gold_lease_x_gm_round_to_4, goldLeaseV2MyOrders.totalEarnings.orZero()
        )
        binding.tvMyOrdersTodayEarnings.setHtmlText(goldLeaseV2MyOrders.todayEarnings.orEmpty())

        binding.tvMyOrdersLeasedGoldTitle.setHtmlText(goldLeaseV2MyOrders.leasedGoldComponent?.title.orEmpty())
        binding.tvMyOrdersLeasedValue.setHtmlText(goldLeaseV2MyOrders.leasedGoldComponent?.value.orEmpty())

        binding.tvMyOrdersEarningsTitle.setHtmlText(goldLeaseV2MyOrders.earnedGoldComponent?.title.orEmpty())
        binding.tvMyOrdersEarningsValue.setHtmlText(goldLeaseV2MyOrders.earnedGoldComponent?.value.orEmpty())

        Glide.with(requireContext()).load(goldLeaseV2MyOrders.subtitleComponent?.iconLink.orEmpty())
            .into(binding.ivMyOrdersSubIcon)
        binding.tvMyOrdersSubTitle.setHtmlText(goldLeaseV2MyOrders.subtitleComponent?.description.orEmpty())

        binding.btnStartNewLease.setText(goldLeaseV2MyOrders.ctaText.orEmpty())

        binding.lottieView.cancelAnimation()
        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(), GoldLeaseConstants.LottieUrls.MY_ORDERS_COIN
        )
        binding.lottieView.addAnimatorListener(lottieAnimator)

        analyticsApi.postEvent(
            GoldLeaseEventKey.LeaseMyOrdersScreen.Lease_InfoScreenMyOrdersShown,
            mapOf(
                GoldLeaseEventKey.Properties.FROM_FLOW to getFlowType(),
                GoldLeaseEventKey.Properties.TOTAL_EARNINGS to goldLeaseV2MyOrders.totalEarnings.orZero(),
                GoldLeaseEventKey.Properties.LEASED_GOLD to goldLeaseV2MyOrders.leasedGoldComponent?.value.orEmpty(),
                GoldLeaseEventKey.Properties.EARNINGS_ADDED to goldLeaseV2MyOrders.earnedGoldComponent?.value.orEmpty(),
                GoldLeaseEventKey.Properties.ONGOING_LEASE_COUNT to goldLeaseV2MyOrders.ongoingLeaseCount.orZero()
            )
        )
    }

    override fun onDestroyView() {
        binding.lottieView.removeAnimatorListener(lottieAnimator)
        super.onDestroyView()
    }
}