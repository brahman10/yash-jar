package com.jar.gold_price_alerts.impl.ui.alert_bottomsheet


import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.util.applyRoundedRectBackground
import com.jar.app.feature_gold_price_alerts.R
import com.jar.app.feature_gold_price_alerts.databinding.FragmentGoldPriceAlertsBottomSheetBinding
import com.jar.feature_gold_price_alerts.shared.domain.model.GoldTrendBottomSheetStaticData
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.Cross_clicked
import com.jar.feature_gold_price_alerts.shared.util.GoldPriceAlertsConstants.AnalyticKeys.Set_Price_Alert
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class SetGoldPriceAlertsBottomSheetFragment :
    BaseBottomSheetDialogFragment<FragmentGoldPriceAlertsBottomSheetBinding>() {

    private var selectablePillAdapter: SelectablePillAdapter? = null

    private val viewModelProvider by viewModels<SetGoldAlertsBottomSheetViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val spaceItemDecorationFilters = SpaceItemDecoration(4.dp, 0.dp)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldPriceAlertsBottomSheetBinding
        get() = FragmentGoldPriceAlertsBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun setup() {
        setupUI()
        setupObservers()
        setupListeners()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    private fun getData() {
        viewModel.fetchBottomSheetStaticDataUseCase()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.bottomSheetStaticDataFlow.collectUnwrapped(
                    onSuccess = {
                        setData(it.data)
                    })
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.amountPillsListFlow.collectLatest {
                    selectablePillAdapter?.submitList(it)
                    binding.btnAction.setDisabled(!(it.any { it.isSelected }))
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.createGoldPriceAlertFlow.collectUnwrapped(
                    onSuccess = {
                        findNavController().previousBackStackEntry?.savedStateHandle?.set(
                            GoldPriceAlertsConstants.Constants.UPDATE_STATE_FROM_BOTTOMSHEET,
                            true
                        )
                        dismissAllowingStateLoss()
                    }
                )
            }
        }
    }

    private fun setData(data: GoldTrendBottomSheetStaticData?) {
        binding.label.text = data?.title
        binding.label2.text = data?.description
        binding.livePrice.setHtmlText(data?.liveGoldPriceText.orEmpty())
        binding.footerText.text = data?.footerText
        Glide.with(requireContext())
            .load(data?.footerIconUrl)
            .into(binding.bottomImage)
        binding.btnAction.setText(data?.saveGoldCta?.title.orEmpty())
        data?.pricePills?.let {
            viewModel.setPriceList(it)
        }
    }

    private fun setupListeners() {
        binding.icClose.setDebounceClickListener {
            viewModel.postEventForClick(Cross_clicked)
            dismissAllowingStateLoss()
        }
        binding.btnAction.setDebounceClickListener {
            viewModel.postEventForClick(Set_Price_Alert)
            viewModel.submitPriceAlert()
        }
    }

    private fun setupUI() {
        applyRoundedRectBackground(
            targetView = binding.clRoot,
            bgColor = ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_2e2942),
            topLeftRadius = 16f.dp,
            topRightRadius = 16f.dp
        )
        selectablePillAdapter = SelectablePillAdapter {
            viewModel.setSelected(it)
        }
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.amountPills.adapter = selectablePillAdapter
        binding.amountPills.layoutManager = layoutManager
        binding.amountPills.addItemDecorationIfNoneAdded(spaceItemDecorationFilters)
    }
}