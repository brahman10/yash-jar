package com.jar.app.feature_gold_lease.impl.ui.lease_plans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.orFalse
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_base.domain.model.GoldBalance
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideTopToReveal
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_gold_lease.GoldLeaseNavigationDirections
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeaseV2LeasePlansBinding
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2JewellerListing
import com.jar.app.feature_gold_lease.impl.ui.lease_plans.adapter.GoldLeaseV2FiltersAdapter
import com.jar.app.feature_gold_lease.impl.ui.lease_plans.adapter.GoldLeaseV2JewellerListingsAdapter
import com.jar.app.feature_gold_lease.impl.ui.lease_plans.adapter.GoldLeaseV2PlansAdapter
import com.jar.app.feature_gold_lease.shared.domain.model.LeasePlanListingFilterEnum
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseV2LeasePlansViewModel
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseEventKey
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class GoldLeaseV2LeasePlansFragment :
    BaseFragment<FragmentGoldLeaseV2LeasePlansBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private var goldLeaseV2JewellerListingsAdapter: GoldLeaseV2JewellerListingsAdapter? = null

    private val spaceItemDecorationJewellerListings = SpaceItemDecoration(5.dp, 0.dp)

    private var goldLeaseV2FiltersAdapter: GoldLeaseV2FiltersAdapter? = null

    private val spaceItemDecorationFilters = SpaceItemDecoration(8.dp, 0.dp)

    private var goldLeaseV2PlansAdapter: GoldLeaseV2PlansAdapter? = null

    private val spaceItemDecorationPlans = SpaceItemDecoration(0.dp, 9.dp)

    private val args by navArgs<GoldLeaseV2LeasePlansFragmentArgs>()

    private val viewModelProvider by viewModels<GoldLeaseV2LeasePlansViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeaseV2LeasePlansBinding
        get() = FragmentGoldLeaseV2LeasePlansBinding::inflate

    private var lockerValueAnimatorJob: Job? = null

    private var isFirstLockerValueAnimation = true

    private var isShownEventSynced = false

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
        observeLiveData()
        setupUI()
        setupListeners()
    }

    private fun setupListeners() {
        binding.clGoldBalance.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.LeasePlansScreen.Lease_ListingsScreenRandomClick,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.USER_TYPE to if (args.isNewLeaseUser) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                    GoldLeaseEventKey.Properties.ELEMENT_TYPE_RANDOM to GoldLeaseEventKey.Values.GOLD_IN_LOCKER,
                    GoldLeaseEventKey.Properties.DATA to viewModel.goldBalanceFlow.value.data?.data?.volume.orZero()
                )
            )
        }
    }

    private fun setupUI() {
        setupToolbar()

        //Setup JewellerListings Adapter
        goldLeaseV2JewellerListingsAdapter = GoldLeaseV2JewellerListingsAdapter()
        binding.rvJewellerIcons.adapter = goldLeaseV2JewellerListingsAdapter
        binding.rvJewellerIcons.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.rvJewellerIcons.addItemDecorationIfNoneAdded(spaceItemDecorationJewellerListings)

        //Setup Filters Adapter
        goldLeaseV2FiltersAdapter = GoldLeaseV2FiltersAdapter {
            analyticsApi.postEvent(
                GoldLeaseEventKey.LeasePlansScreen.Lease_ListingsScreenClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.USER_TYPE to if (args.isNewLeaseUser) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                    GoldLeaseEventKey.Properties.BUTTON_TYPE to GoldLeaseEventKey.Values.FILTER,
                    GoldLeaseEventKey.Properties.TITLE to it.leasePlanListingFilterName.orEmpty()
                )
            )
            binding.tvNoLeaseTitle.setHtmlText(
                it.noLeaseTitle ?: getString(R.string.feature_gold_lease_no_leases_available)
            )
            binding.tvNoLeaseDescription.setHtmlText(
                it.noLeaseDescription ?: getString(R.string.feature_gold_lease_come_back_later)
            )
            binding.rvPlans.isVisible = it.leasePlanCount != 0
            binding.llNoLease.isVisible = it.leasePlanCount == 0
            viewModel.updateSelectedFilter(it)
        }
        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        layoutManager.alignItems = AlignItems.FLEX_START
        binding.rvFilters.adapter = goldLeaseV2FiltersAdapter
        binding.rvFilters.layoutManager = layoutManager
        binding.rvFilters.addItemDecorationIfNoneAdded(spaceItemDecorationFilters)

        //Setup Plans Adapter
        goldLeaseV2PlansAdapter = GoldLeaseV2PlansAdapter(
            onInfoClicked = {
                analyticsApi.postEvent(
                    GoldLeaseEventKey.LeasePlansScreen.Lease_ListingsScreenClicked,
                    mapOf(
                        GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                        GoldLeaseEventKey.Properties.USER_TYPE to if (args.isNewLeaseUser) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                        GoldLeaseEventKey.Properties.BUTTON_TYPE to GoldLeaseEventKey.Values.INFO_ICON,
                        GoldLeaseEventKey.Properties.SELECTED_EARNINGS to it.earningsPercentage.orZero(),
                        GoldLeaseEventKey.Properties.SELECTED_JEWELLER_TITLE to it.jewellerName.orEmpty(),
                        GoldLeaseEventKey.Properties.SELECTED_FILLING_STAGE to it.getLeasePlanCapacityStatus().name,
                        GoldLeaseEventKey.Properties.SELECTED_JAR_BONUS to it.bonusPercentage.orZero(),
                        GoldLeaseEventKey.Properties.SELECTED_MINIMUM_QUANTITY to it.minimumQuantityComponent?.value.orEmpty(),
                        GoldLeaseEventKey.Properties.SELECTED_LOCK_IN_PERIOD to it.lockInComponent?.value.orEmpty()
                    )
                )
                navigateTo(
                    GoldLeaseV2LeasePlansFragmentDirections.actionToGoldLeaseV2JewellerDetailsBottomSheetFragment(
                        flowType = args.flowType,
                        jewellerId = it.jewellerId.orEmpty()
                    )
                )
            },
            onSelectClicked = {
                analyticsApi.postEvent(
                    GoldLeaseEventKey.LeasePlansScreen.Lease_ListingsScreenClicked,
                    mapOf(
                        GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                        GoldLeaseEventKey.Properties.USER_TYPE to if (args.isNewLeaseUser) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                        GoldLeaseEventKey.Properties.BUTTON_TYPE to GoldLeaseEventKey.Values.SELECT,
                        GoldLeaseEventKey.Properties.SELECTED_EARNINGS to it.earningsPercentage.orZero(),
                        GoldLeaseEventKey.Properties.SELECTED_JEWELLER_TITLE to it.jewellerName.orEmpty(),
                        GoldLeaseEventKey.Properties.SELECTED_FILLING_STAGE to it.getLeasePlanCapacityStatus().name,
                        GoldLeaseEventKey.Properties.SELECTED_JAR_BONUS to it.bonusPercentage.orZero(),
                        GoldLeaseEventKey.Properties.SELECTED_MINIMUM_QUANTITY to it.minimumQuantityComponent?.value.orEmpty(),
                        GoldLeaseEventKey.Properties.SELECTED_LOCK_IN_PERIOD to it.lockInComponent?.value.orEmpty()
                    )
                )
                navigateTo(
                    GoldLeaseV2LeasePlansFragmentDirections.actionGoldLeaseV2LeasePlansFragmentToGoldLeaseV2OrderDetailFragment(
                        flowType = args.flowType,
                        leasePlan = it,
                        isNewLeaseUser = args.isNewLeaseUser
                    )
                )
            },
            onRandomElementClicked = { elementName, data ->
                analyticsApi.postEvent(
                    GoldLeaseEventKey.LeasePlansScreen.Lease_ListingsScreenRandomClick,
                    mapOf(
                        GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                        GoldLeaseEventKey.Properties.USER_TYPE to if (args.isNewLeaseUser) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                        GoldLeaseEventKey.Properties.ELEMENT_TYPE_RANDOM to elementName,
                        GoldLeaseEventKey.Properties.DATA to data
                    )
                )
            }
        )
        binding.rvPlans.adapter = goldLeaseV2PlansAdapter
        binding.rvPlans.layoutManager = LinearLayoutManager(context)
        binding.rvPlans.addItemDecorationIfNoneAdded(spaceItemDecorationPlans)
    }

    private fun getData() {
        viewModel.fetchJewellerListings()
        viewModel.fetchUserGoldBalance()
        viewModel.fetchGoldLeaseFiltersList()
    }

    private fun observeLiveData() {
        observeJewellerListingsLiveData()
        observeUserGoldBalanceLiveData()
        observeGoldLeaseFiltersLiveData()
        observeLeasePlansFlow()
        observeGoldBalanceAndFilterLiveData()
    }

    private fun getLeasePlansList(filter: String) {
        viewModel.fetchGoldLeasePlans(filter)
    }

    private fun observeGoldBalanceAndFilterLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goldBalanceAndFiltersFlow.collect {
                    if (isShownEventSynced.not() && it != null) {
                        isShownEventSynced = true
                        val allFilters =
                            it.second.leasePlanFilterInfoList?.joinToString(",") { leasePlan -> leasePlan.leasePlanListingFilterName.orEmpty() }
                        analyticsApi.postEvent(
                            GoldLeaseEventKey.LeasePlansScreen.Lease_ListingsScreenShown,
                            mapOf(
                                GoldLeaseEventKey.Properties.USER_TYPE to if (args.isNewLeaseUser) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                                GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                                GoldLeaseEventKey.Properties.GOLD_IN_LOCKER to it.first.volume.orZero(),
                                GoldLeaseEventKey.Properties.TAB to allFilters.orEmpty(),
                                GoldLeaseEventKey.Properties.DEFAULT_TAB to it.second.leasePlanFilterInfoList?.find { it.defaultFilter.orFalse() }?.leasePlanListingFilterName.orEmpty()
                            )
                        )
                    }
                }
            }
        }
    }

    private fun observeLeasePlansFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.goldLeasePlansFlow.collectLatest {
                    goldLeaseV2PlansAdapter?.submitData(it)
                }
            }
        }
    }

    private fun observeGoldLeaseFiltersLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goldLeaseFiltersListFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = { filtersList ->
                        dismissProgressBar()
                        filtersList?.let {
                            goldLeaseV2FiltersAdapter?.submitList(it.leasePlanFilterInfoList.orEmpty())
                            it.leasePlanFilterInfoList?.find { filter -> filter.isSelected }
                                ?.let { plan ->
                                    getLeasePlansList(plan.getLeasePlanListingFilter())
                                } ?: kotlin.run {
                                it.leasePlanFilterInfoList?.find { filter -> filter.defaultFilter.orFalse() }
                                    ?.let { plan ->
                                        getLeasePlansList(plan.getLeasePlanListingFilter())
                                    } ?: kotlin.run {
                                    getLeasePlansList(LeasePlanListingFilterEnum.ALL.name)
                                }
                            }
                        }
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }

    private fun observeUserGoldBalanceLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goldBalanceFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setupGoldBalanceDataInUI(it)
                        }
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }

    private fun observeJewellerListingsLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.jewellerListingsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setupJewellerListingsDataInUI(it)
                        }
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }

    private fun setupGoldBalanceDataInUI(goldBalance: GoldBalance) {
        binding.clGoldBalance.isVisible = goldBalance.volume.orZero() != 0.0f
        if (goldBalance.volume.orZero() != 0.0f) {
            binding.tvLockerGoldGm.text = goldBalance.getGoldVolumeWithUnit()
            binding.tvLockerGoldRs.text = getString(
                R.string.feature_gold_lease_rupee_prefix_float, goldBalance.currentValue.orZero()
            )
            startLockerValueAnimation()
        }
    }

    private fun startLockerValueAnimation() {
        lockerValueAnimatorJob?.cancel()
        lockerValueAnimatorJob = uiScope.launch {
            if (isFirstLockerValueAnimation) {
                isFirstLockerValueAnimation = false
                binding.tvLockerGoldRs.slideTopToReveal(
                    binding.tvLockerGoldGm,
                    onAnimationEnd = {
                        startLockerValueAnimation()
                    }
                )
            } else {
                delay(1000)
                if (binding.tvLockerGoldRs.isVisible) {
                    binding.tvLockerGoldRs.slideTopToReveal(
                        binding.tvLockerGoldGm,
                        onAnimationEnd = {
                            binding.tvLockerGoldRs.isInvisible = true
                            startLockerValueAnimation()
                        }
                    )
                } else {
                    binding.tvLockerGoldGm.slideTopToReveal(
                        binding.tvLockerGoldRs,
                        onAnimationEnd = {
                            binding.tvLockerGoldGm.isInvisible = true
                            startLockerValueAnimation()
                        }
                    )
                }
            }
        }
    }

    private fun setupJewellerListingsDataInUI(goldLeaseV2JewellerListing: GoldLeaseV2JewellerListing) {
        binding.tvJewellerListingsTitle.setHtmlText(goldLeaseV2JewellerListing.title.orEmpty())
        binding.tvSocialProofText.setHtmlText(goldLeaseV2JewellerListing.socialProofText.orEmpty())
        goldLeaseV2JewellerListingsAdapter?.submitList(
            goldLeaseV2JewellerListing.jewellerIcons.orEmpty().distinct()
        )
        Glide.with(requireContext()).load(goldLeaseV2JewellerListing.socialProofIcon.orEmpty())
            .into(binding.ivSocialProofLogo)
    }

    private fun setupToolbar() {
        binding.toolbar.root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.bgColor
            )
        )
        binding.toolbar.tvTitle.isVisible = true
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false

        binding.toolbar.separator.isVisible = true

        binding.toolbar.tvTitle.text = getString(R.string.feature_gold_lease_goldx_lease_plans)

        //Setup FAQ Button
        binding.toolbar.tvEnd.setBackgroundResource(com.jar.app.core_ui.R.drawable.bg_rounded_40_121127)
        binding.toolbar.tvEnd.setPadding(16.dp, 8.dp, 16.dp, 8.dp)
        binding.toolbar.tvEnd.setTextColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.color_EEEAFF
            )
        )
        binding.toolbar.tvEnd.text = getString(R.string.feature_gold_lease_faqs)
        binding.toolbar.tvEnd.isVisible = true

        binding.toolbar.tvEnd.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.CommonEvents.Lease_FAQButtonClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.SCREEN_NAME to GoldLeaseEventKey.Screens.LISTING_SCREEN
                )
            )
            navigateTo(
                GoldLeaseNavigationDirections.actionToGoldLeaseFaqBottomSheetFragment(
                    flowType = args.flowType
                )
            )
        }

        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.CommonEvents.Lease_BackButtonClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.SCREEN_NAME to GoldLeaseEventKey.Screens.LISTING_SCREEN
                )
            )
            popBackStack()
        }
    }

    override fun onDestroyView() {
        isFirstLockerValueAnimation = true
        lockerValueAnimatorJob?.cancel()
        isShownEventSynced = false
        super.onDestroyView()
    }
}