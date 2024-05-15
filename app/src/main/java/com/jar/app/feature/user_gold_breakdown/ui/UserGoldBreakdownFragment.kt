package com.jar.app.feature.user_gold_breakdown.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.R
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.*
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.databinding.FragmentUserGoldBreakdownBinding
import com.jar.app.feature_homepage.shared.domain.model.user_gold_breakdown.BreakdownDataUnit
import com.jar.app.core_base.domain.model.GoldBalance
import com.jar.app.core_base.domain.model.GoldBalanceViewType
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.roundUp
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_homepage.shared.ui.user_gold_breakdown.UserGoldBreakdownFragmentViewModel
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class UserGoldBreakdownFragment :
    BaseBottomSheetDialogFragment<FragmentUserGoldBreakdownBinding>() {

    private val viewModelProvider by viewModels<UserGoldBreakdownFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val args by navArgs<UserGoldBreakdownFragmentArgs>()

    private val adapter = UserGoldBreakdownAdapter()

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 8.dp)

    private var breakdownDataUnit: BreakdownDataUnit = BreakdownDataUnit.GRAM
    private fun getBreakDownFromGoldBalanceType(whichBalanceView: GoldBalanceViewType): BreakdownDataUnit {
        return when (whichBalanceView) {
            GoldBalanceViewType.ONLY_RS, GoldBalanceViewType.RS_ND_GM -> BreakdownDataUnit.AMOUNT
            GoldBalanceViewType.ONLY_GM, GoldBalanceViewType.GM_ND_RS -> BreakdownDataUnit.GRAM
        }
    }

    override val bottomSheetConfig = DEFAULT_CONFIG

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUserGoldBreakdownBinding
        get() = FragmentUserGoldBreakdownBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setup() {
        val whichBalanceView = GoldBalanceViewType.valueOf(args.whichBalanceView)
        breakdownDataUnit = getBreakDownFromGoldBalanceType(whichBalanceView)
        setupUI(whichBalanceView)
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupUI(whichBalanceView: GoldBalanceViewType) {
        when (whichBalanceView) {
            GoldBalanceViewType.ONLY_RS -> {
                binding.tvHeaderTotalGoldPurchased.text =
                    getString(R.string.feature_home_page_total_value)
            }

            GoldBalanceViewType.ONLY_GM -> {
                binding.tvHeaderTotalGoldPurchased.text =
                    getString(com.jar.app.feature_homepage.R.string.feature_homepage_your_savings)
            }

            GoldBalanceViewType.GM_ND_RS -> {
                binding.tvHeaderTotalGoldPurchased.text =
                    getString(com.jar.app.feature_homepage.R.string.feature_homepage_your_savings)
            }

            GoldBalanceViewType.RS_ND_GM -> {
                binding.tvHeaderTotalGoldPurchased.text =
                    getString(R.string.feature_home_page_total_value)
            }
        }
        binding.rvData.layoutManager = LinearLayoutManager(context)
        binding.rvData.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvData.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnToggleUnit.setOnClickListener {
            toggleDataUnit()
        }

        binding.btnCross.setDebounceClickListener {
            dismiss()
            analyticsHandler.postEvent(EventKey.CLICKED_CLOSE_AMOUNT_BREAKDOWN)
        }
    }

    private fun toggleDataUnit() {
        if (breakdownDataUnit == BreakdownDataUnit.GRAM) {
            breakdownDataUnit = BreakdownDataUnit.AMOUNT
            binding.btnToggleUnit.text = getString(R.string.value_in_rupees)
        } else {
            breakdownDataUnit = BreakdownDataUnit.GRAM
            binding.btnToggleUnit.text = getString(R.string.value_in_grams)
        }
        viewModel.setDataUnit(breakdownDataUnit)
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.goldBalanceLiveData.collect(
                    onSuccess = {
                        it?.let { setGoldBalance(it) }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(getRootView())
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.userGoldBreakdownLiveData.collectUnwrapped(
                    onSuccess = {
                        binding.shimmerPlaceholder.stopShimmer()
                        binding.shimmerPlaceholder.isVisible = false
                        binding.rvData.isVisible = true
                        adapter.submitList(it?.userGoldBreakdownList)
                        when (breakdownDataUnit) {
                            BreakdownDataUnit.AMOUNT -> {
                                binding.tvTotalGold.text = getString(
                                    R.string.rupee_x_in_double,
                                    it?.totalAmount?.roundUp(2)
                                )
                                binding.tvGoldBalance.text =
                                    it?.totalAmount?.roundUp(2)?.getFormattedAmount()?.let { it1 ->
                                        getCustomStringFormatted(
                                            MR.strings.feature_buy_gold_v2_currency_sign_x_string,
                                            it1
                                        )
                                    }
                            }

                            BreakdownDataUnit.GRAM -> {
                                binding.tvTotalGold.text =
                                    viewModel.goldBalanceLiveData.value.data?.data?.getGoldVolumeWithUnit()
                            }
                        }
                        binding.clGoldLeaseBreakDown.isVisible =
                            viewModel.goldBalanceLiveData.value.data?.data?.goldLeaseBreakupObject != null
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(getRootView())
                    }
                )
            }
        }
    }

    private fun setGoldBalance(data: GoldBalance) {
        when (data.getBalanceViewData()) {
            GoldBalanceViewType.ONLY_GM, GoldBalanceViewType.GM_ND_RS -> {
                val goldBalance = if (data.unitPreference == "mg")
                    requireContext().getString(
                        R.string.n_string_mg_gold,
                        data.volumeInMg?.volumeToString(1)
                    )
                else requireContext().getString(
                    R.string.n_string_gm_gold,
                    data.volume.volumeToString()
                )
                binding.tvGoldBalance.text = goldBalance
            }

            else -> {}
        }

        //Set Gold Lease Strip
        when (breakdownDataUnit) {
            BreakdownDataUnit.AMOUNT -> {
                data.goldLeaseBreakupObject?.let { goldLeaseDetails ->
                    binding.tvGoldLeasedValue.text = getString(
                        R.string.minus_rupee_x_round_to_2,
                        goldLeaseDetails.amountLeased.orZero()
                    )
                }
            }

            BreakdownDataUnit.GRAM -> {
                data.goldLeaseBreakupObject?.let { goldLeaseDetails ->
                    binding.tvGoldLeasedValue.text = getString(
                        R.string.minus_x_gm_round_to_4,
                        data.getGoldVolumeWithUnit(
                            volumeInMg = goldLeaseDetails.volumeLeased.orZero(),
                            volume = goldLeaseDetails.volumeLeased.orZero()
                        )
                    )
                }
            }
        }
        viewModel.fetchUserGoldBreakDown(
            data.getBalanceViewData(),
            convertToString = { stringResource, data ->
                getCustomStringFormatted(
                    requireContext(),
                    stringResource,
                    data
                )
            }
        )
    }

    private fun getData() {
        viewModel.fetchUserGoldBalance()
    }
}