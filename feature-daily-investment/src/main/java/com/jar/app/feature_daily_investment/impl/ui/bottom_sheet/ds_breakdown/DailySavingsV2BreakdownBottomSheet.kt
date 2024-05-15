package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.ds_breakdown

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.util.TypefaceContainer
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.shared.domain.model.DailySavingsBreakdownData
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentDailySavingsBreakdownBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.DAYS_IN_MONTH
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.JAR_1_MONTHS
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.JAR_2_MONTHS
import com.jar.app.feature_daily_investment.impl.util.DailySavingConstants.JAR_3_MONTHS
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
internal class DailySavingsV2BreakdownBottomSheet :
    BaseBottomSheetDialogFragment<FeatureDailyInvestmentDailySavingsBreakdownBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val viewModel by viewModels<DailySavingsV2BreakdownViewModel> { defaultViewModelProviderFactory }

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 7.dp)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentDailySavingsBreakdownBinding
        get() = FeatureDailyInvestmentDailySavingsBreakdownBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isCancellable = false,
            isDraggable = false,
        )

    private val args: DailySavingsV2BreakdownBottomSheetArgs by navArgs()

    private var activityRef: WeakReference<Context>? = null

    private var dSTime: Int = 0

    private var currentPrice: Float = 0.0.toFloat()

    override fun setup() {
        setupUI()
        getData()
        observeData()
        setupListeners()
    }

    private fun setupUI() {
        binding.chip1.setHtmlText( resources.getQuantityString(
            R.plurals.feature_daily_investment_n_months,
            3,
            3
        ))
        TypefaceContainer(
            null,
            com.jar.app.core_ui.R.font.inter_bold
        ).applyTo(binding.chip1)

        binding.chip2.setHtmlText(resources.getQuantityString(
            R.plurals.feature_daily_investment_n_months,
            6,
            6
        ))
        TypefaceContainer(
            null,
            com.jar.app.core_ui.R.font.inter_bold
        ).applyTo(binding.chip2)

        binding.chip3.setHtmlText(resources.getQuantityString(
            R.plurals.feature_daily_investment_n_months,
            9,
            9
        ))
        TypefaceContainer(
            null,
            com.jar.app.core_ui.R.font.inter_bold
        ).applyTo(binding.chip3)

    }

    private fun getData() {
        dSTime = args.dsTime
        activityRef = WeakReference(requireActivity())
        viewModel.fetchGoldPrice()
    }

    private fun onTimeSelectionUpdateGeneratorCalculation() {
        viewModel.calculateGoldAmount(
            context = activityRef!!,
            months = dSTime / DAYS_IN_MONTH,
            dailyInvestment = args.dsAmount
        )
        findNavController().getBackStackEntry(R.id.dailySavingsV2Fragment).savedStateHandle[DailySavingConstants.BREAK_DOWN_TIME_SELECTION] =
            dSTime
    }

    fun setupListeners() {
        binding.btnClose.setDebounceClickListener {
            dismiss()
        }
        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->

            val chip: Chip? =
                group.findViewById(checkedId)

            chip?.let {
                if (binding.chip1.id == checkedId) {
                    dSTime = JAR_1_MONTHS * DAYS_IN_MONTH
                    fireClickEvent(getString(R.string.daily_investment_amount_selection_jar1_label))
                    onTimeSelectionUpdateGeneratorCalculation()
                }
                if (binding.chip2.id == checkedId) {
                    dSTime = JAR_2_MONTHS * DAYS_IN_MONTH
                    onTimeSelectionUpdateGeneratorCalculation()
                    fireClickEvent(getString(R.string.daily_investment_amount_selection_jar2_label))
                }
                if (binding.chip3.id == checkedId) {
                    dSTime = JAR_3_MONTHS * DAYS_IN_MONTH
                    onTimeSelectionUpdateGeneratorCalculation()
                    fireClickEvent(getString(R.string.daily_investment_amount_selection_jar3_label))
                }
            }
        }
    }

    private fun setData(data: DailySavingsBreakdownData) {

        val adapter = DailySavingsBreakdownAdapter()

        binding.tvHeading.text = data.heading
        binding.tvSubHeading1.text = data.subHeading1

        binding.rvBreakdown1.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBreakdown1.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvBreakdown1.adapter = adapter
        adapter.submitList(data.breakDownSummary)

        binding.tvSubHeading2.text = data.subHeading2

        val adapter2 = DailySavingsBreakdownAdapter()
        binding.rvBreakdown2.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBreakdown2.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvBreakdown2.adapter = adapter2
        adapter2.submitList(data.breakDownDetails)


        binding.tvGST.text = data.gst
        binding.tvWarning.text = data.warning
    }

    private fun observeData() {
        viewModel.fetchCurrentGoldPriceResponse.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                binding.shimmerPlaceholder.isVisible = true
                binding.clContainer.isVisible = false
                binding.shimmerPlaceholder.startShimmer()
            },
            onSuccess = {
                binding.shimmerPlaceholder.isVisible = false
                binding.clContainer.isVisible = true
                binding.shimmerPlaceholder.stopShimmer()
                it.price.let { goldPrice ->
                    currentPrice = goldPrice
                    when (dSTime / DAYS_IN_MONTH) {
                        JAR_1_MONTHS -> {
                            binding.chipGroup.check(binding.chip1.id)
                        }
                        JAR_2_MONTHS -> {
                            binding.chipGroup.check(binding.chip2.id)
                        }
                        JAR_3_MONTHS -> {
                            binding.chipGroup.check(binding.chip3.id)
                        }
                    }

                }
            },
            onError = {
                binding.shimmerPlaceholder.isVisible = false
                binding.clContainer.isVisible = true
                binding.shimmerPlaceholder.stopShimmer()
            }

        )
        viewModel.dailySavingsBreakdownBottomSheetLiveData.observe(viewLifecycleOwner) {
            setupListeners()
            setData(it)
        }

    }


    private fun fireClickEvent(monthString: String) {
        analyticsApi.postEvent(
            DailySavingsEventKey.Shown_DailySaving_Breakdown_BottomSheet,
            mapOf(
                DailySavingsEventKey.ButtonType to monthString,
            )
        )
    }

}