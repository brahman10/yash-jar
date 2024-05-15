package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_utils.data.MinMaxFilter
import com.jar.app.core_utils.data.NumberOnlyInputFilter
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentFragmentSetupDailyInvestmentBottomSheetBinding
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.feature_daily_investment.api.util.EventKey
import com.jar.app.feature_daily_investment.impl.ui.SuggestedAmountAdapter
import com.jar.app.feature_daily_investment.impl.ui.setup_daily_investment.SetupDailyInvestmentFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SetupDailyInvestmentBottomSheet :
    BaseBottomSheetDialogFragment<FeatureDailyInvestmentFragmentSetupDailyInvestmentBottomSheetBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModel by viewModels<SetupDailyInvestmentFragmentViewModel> { defaultViewModelProviderFactory }

    private var adapter: SuggestedAmountAdapter? = null

    private val spaceItemDecoration = SpaceItemDecoration(3.dp, 0.dp)

    private val args by navArgs<SetupDailyInvestmentBottomSheetArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentFragmentSetupDailyInvestmentBottomSheetBinding
        get() = FeatureDailyInvestmentFragmentSetupDailyInvestmentBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG

    override fun getTheme(): Int {
        return com.jar.app.core_ui.R.style.BottomSheetDialogInput
    }

    override fun setup() {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupUI() {
        binding.rvSuggestedAmounts.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        adapter = SuggestedAmountAdapter {
            binding.etBuyAmount.setText("${it.amount.toInt()}")
            binding.etBuyAmount.setSelection(binding.etBuyAmount.text?.length.orZero())

            analyticsHandler.postEvent(
                EventKey.Clicked_Recommendation_DailyInvestmentBottomSheet,
                mapOf(EventKey.recommendedAmount to it.amount.toString())
            )
        }
        binding.rvSuggestedAmounts.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvSuggestedAmounts.adapter = adapter
        binding.etBuyAmount.filters = arrayOf(
            MinMaxFilter(0, 2000),
            NumberOnlyInputFilter()
        )

        binding.etBuyAmount.setText(args.defaultAmount.toString())
        binding.etBuyAmount.setSelection(binding.etBuyAmount.text?.length.orZero())
    }

    private fun setupListeners() {
        binding.btnSet.setDebounceClickListener {
            val amount = binding.etBuyAmount.text
            if (!amount.isNullOrBlank()) {
                if (amount.toString().toFloatOrNull().orZero() <= 2000) {
                    viewModel.enableOrUpdateDailySaving(amount = amount.toString().toFloat())
                    analyticsHandler.postEvent(
                        EventKey.Clicked_SetButton_DailyInvestmentBottomSheet,
                        mapOf(EventKey.amount to amount.toString())
                    )
                } else {
                    getString(R.string.feature_daily_investment_max_amount_cannot_be_more_than_rs_2000)
                        .snackBar(binding.root)
                }
            } else {
                getString(R.string.feature_daily_investment_please_enter_the_valid_amount)
                    .snackBar(binding.root)
            }
        }

        binding.btnClose.setDebounceClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun observeLiveData() {
        viewModel.dsSeekBarLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                viewModel.createRvListData(it)
            }
        )

        viewModel.rVLiveData.observe(viewLifecycleOwner) {
            adapter?.submitList(it)
        }

        viewModel.dailyInvestmentStatusLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                if (binding.etBuyAmount.text.isNullOrBlank())
                    binding.etBuyAmount.setText(if (it.enabled) "${it.amount.toInt()}" else "100")
                binding.etBuyAmount.setSelection(binding.etBuyAmount.text?.length.orZero())
            }
        )

        viewModel.updateDailyInvestmentStatusLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(getRootView()),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                EventBus.getDefault().post(RefreshDailySavingEvent())
                binding.etBuyAmount.hideKeyboard()
                dismissAllowingStateLoss()
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    private fun getData() {
        viewModel.fetchSeekBarData()
    }
}